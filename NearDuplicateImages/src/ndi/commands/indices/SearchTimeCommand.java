package ndi.commands.indices;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Set;

import metricspaces.indices.Index;
import metricspaces.indices.IndexFactory;
import metricspaces.indices.SearchResult;
import metricspaces.indices.SurrogateIndex;
import metricspaces.indices.VPTree;
import metricspaces.util.Progress;
import ndi.files.ClusterReader;
import ndi.files.FileFormatException;
import commandline.Command;
import commandline.ParameterException;
import commandline.Parameters;
import commandline.ProgressReporter;

public class SearchTimeCommand implements Command {
	private Parameters parameters;

	
	@Override
	public void init(Parameters parameters) {
		this.parameters = parameters;
	}

	@Override
	public void run() {
		Progress progress = new Progress();
		FeedbackPrinter feedbackPrinter = null;
		
		try {
			//set up how the statistics will be printed
			if (parameters.getBoolean("csv", false)) {
				feedbackPrinter = new CsvFeedbackPrinter();
			}
			else {
				feedbackPrinter = new ConsoleFeedbackPrinter(progress);
			}
			
			//set up how the index will be searched
			Searcher searcher;
			Index index = IndexFactory.open(parameters.require("index"), false, progress);
			double radius = parameters.getDouble("radius");
			
			if (parameters.get("surrogateRadius") != null && index instanceof SurrogateIndex) {
				searcher = new SurrogateSearcher((SurrogateIndex)index, radius, parameters.getDouble("surrogateRadius"));
			}
			else {
				searcher = new StandardSearcher(index, radius);
			}
			
			//set up the false positive counter if necessary
			FalsePositiveCounter falsePositives = null;
			
			if (parameters.get("clusters") != null) {
				falsePositives = new FalsePositiveCounter(parameters.get("clusters"));
			}
			
			//set up the results writer if necessary
			ResultsWriter resultsWriter = null;
			
			if (parameters.get("results") != null) {
				resultsWriter = new ResultsWriter(parameters.get("results"));
			}
			
			//turn off VP tree optimisation if necessary
			if (!parameters.getBoolean("vpoptimise", true) && index instanceof VPTree)
				((VPTree)index).setOptimise(false);
			
			//go!
			int resultCount = 0;
			int count = parameters.getInt("count", 1000);
			progress.setOperation("Searching", count);
			
			long time = System.currentTimeMillis();
			
			for (int i = 0; i < count; i++) {
				List<SearchResult> results = searcher.nextSearch();
				resultCount += results.size();
				
				if (falsePositives != null)
					falsePositives.count(searcher.getQueryID(), results);
				
				if (resultsWriter != null)
					resultsWriter.write(i, results);
			}
			
			time = System.currentTimeMillis() - time;
			
			//close things off
			index.close();
			
			if (resultsWriter != null)
				resultsWriter.close();
			
			//write out the results
			int calcs;
			Integer surrogateCalcs = null;
			Integer falsePositiveCount = null;
					
			if (index instanceof SurrogateIndex) {
				SurrogateIndex surrogate = (SurrogateIndex)index;
				surrogateCalcs = surrogate.getMetricSpace().getDistanceCount();
				calcs = surrogate.getOriginalSpace().getDistanceCount();
			}
			else {
				calcs = index.getMetricSpace().getDistanceCount();
			}
			
			if (falsePositives != null)
				falsePositiveCount = falsePositives.getCount();
			
			feedbackPrinter.print(count, time, calcs, surrogateCalcs, resultCount, falsePositiveCount);
		}
		catch (ParameterException | IOException | FileFormatException ex) {
			feedbackPrinter.error(ex.getMessage());
		}
	}

	@Override
	public String getName() {
		return "SearchTime";
	}

	@Override
	public String describe() {
		parameters.describe("index", "The index file to use.");
		parameters.describe("radius", "The search radius to use.");
		parameters.describe("count", "The number of searches to make to calculate the average (default 1000).");
		parameters.describe("optimise", "Set to true to search with optimisation.");
		parameters.describe("surrogateRadius", "For surrogate indices: the radius to use when searching the surrogate "
				+ "index (default same as radius, only works if optimise=true).");
		parameters.describe("clusters", "A file containing pairs of points which have been judged to be near duplicates.");
		parameters.describe("csv", "Set to true to have summarised information printed in CSV format.");
		parameters.describe("vpoptimise", "Set to false to skip VP tree search optimisation (default true).");
		parameters.describe("results", "If provided, the search results will be output in CSV format to the specified file.");
		return "Performs a number of searches against an index and calculates the average search time.";
	}
	
	
	private interface FeedbackPrinter {
		void print(int count, long time, int calcs, Integer surrogateCalculations, int results, Integer falsePositives);
		void error(String error);
	}
	
	private class ConsoleFeedbackPrinter implements FeedbackPrinter {
		private ProgressReporter reporter;
		
		public ConsoleFeedbackPrinter(Progress progress) {
			reporter = new ProgressReporter(progress, 250);
		}

		@Override
		public void print(int count, long time, int calcs, Integer surrogateCalculations, int results, Integer falsePositives) {
			reporter.stop();
			System.out.printf("\nAverage statistics over %d searches:\n", count);
			System.out.printf("Time: %.3f ms\n", (double)time / count);
			System.out.printf("Calculations: %.0f\n", (double)calcs / count);
			
			if (surrogateCalculations != null)
				System.out.printf("Surrogate calculations: %.0f\n", surrogateCalculations.doubleValue() / count);
			
			System.out.printf("Results: %.0f\n", (double)results / count);
			
			if (falsePositives != null)
				System.out.printf("False positives: %.0f\n", falsePositives.doubleValue() / count);
		}

		@Override
		public void error(String error) {
			reporter.stop();
			System.err.println(error);
		}
	}
	
	private class CsvFeedbackPrinter implements FeedbackPrinter {
		@Override
		public void print(int count, long time, int calcs, Integer surrogateCalculations, int results, Integer falsePositives) {
			System.out.printf("%.3f,", (double)time / count);
			System.out.printf("%.0f,", (double)calcs / count);
			
			if (surrogateCalculations != null)
				System.out.printf("%.0f,", surrogateCalculations.doubleValue() / count);
			else
				System.out.printf("0,");
			
			System.out.printf("%.0f", (double)results / count);
			
			if (falsePositives != null)
				System.out.printf(",%.0f", falsePositives.doubleValue() / count);
			
			System.out.println();
		}

		@Override
		public void error(String error) {
			System.err.println(error);
		}
	}
	
	
	private interface Searcher {
		List<SearchResult> nextSearch();
		int getQueryID();
	}
	
	private class StandardSearcher implements Searcher {
		private Index index;
		private double radius;
		private int i;
		
		public StandardSearcher(Index index, double radius) {
			this.index = index;
			this.radius = radius;
			i = -1;
		}
		
		@Override
		public List<SearchResult> nextSearch() {
			return index.search(++i, radius);
		}

		@Override
		public int getQueryID() {
			return i;
		}
	}
	
	
	private class SurrogateSearcher implements Searcher {
		private SurrogateIndex index;
		private double radius, surrogateRadius;
		private int i;
		
		public SurrogateSearcher(SurrogateIndex index, double radius, double surrogateRadius) {
			this.index = index;
			this.radius = radius;
			this.surrogateRadius = surrogateRadius;
			i = -1;
		}
		
		@Override
		public List<SearchResult> nextSearch() {
			return index.search(++i, radius, surrogateRadius);
		}

		@Override
		public int getQueryID() {
			return i;
		}
	}
	
	
	private class FalsePositiveCounter {
		private Map<Integer, Set<Integer>> clusters;
		private int count;
		
		public FalsePositiveCounter(String path) throws IOException, FileFormatException {
			ClusterReader reader = new ClusterReader(path, true);
			clusters = reader.read();
			reader.close();
		}
		
		public void count(int queryId, List<SearchResult> results) {
			Set<Integer> cluster = clusters.get(queryId);
        	
        	if (cluster != null) {
            	for (SearchResult result: results) {
            		if (!cluster.contains(result.getResult()))
            			count++;
            	}
        	}
        	else {
        		count += results.size();
        	}
		}
		
		public int getCount() {
			return count;
		}
	}
	
	
	private class ResultsWriter {
		private BufferedWriter writer;
		
		public ResultsWriter(String path) throws IOException {
			writer = new BufferedWriter(new FileWriter(path));
			writer.write("search,result,distance");
		}
		
		public void write(int i, List<SearchResult> results) throws IOException {
			for (SearchResult result: results) {
				writer.write(String.format("%d,%d,%f\n", i, result.getResult(), result.getDistance()));
			}
		}
		
		public void close() throws IOException {
			writer.close();
		}
	}
}
