package ndi.commands.indexes;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Set;

import metricspaces.Progress;
import metricspaces.descriptors.Descriptor;
import metricspaces.files.DescriptorFile;
import metricspaces.indexes.Index;
import metricspaces.indexes.SearchResult;
import metricspaces.indexes.SurrogateSpaceIndex;
import ndi.files.ClusterReader;
import ndi.files.FileFormatException;
import ndi.files.IndexFileOpener;

import commandline.Command;
import commandline.ParameterException;
import commandline.Parameters;
import commandline.ProgressReporter;

public class SearchTimeCommand implements Command {
	private Parameters parameters;
	private IndexFileOpener indexOpener;

	
	@Override
	public void init(Parameters parameters) {
		this.parameters = parameters;
		indexOpener = new IndexFileOpener(parameters);
	}

	@Override
	public void run() {
		Progress progress = new Progress();
		ProgressReporter reporter = new ProgressReporter(progress,  250);
		
		try {
			Index index = indexOpener.open(progress);
			DescriptorFile objects = index.getObjects();
			
			double radius = parameters.getDouble("radius");
			int count = parameters.getInt("count", 1000);
			boolean optimise = parameters.getBoolean("optimise", false);
			long resultCount = 0, falsePositiveCount = 0;
			
			Map<Integer, Set<Integer>> clusters = null;
			
			if (parameters.get("clusters") != null) {
				ClusterReader reader = new ClusterReader(parameters.require("clusters"), true);
				clusters = reader.read();
				reader.close();
			}
			
			progress.setOperation("Searching", count);
			
			long time = System.currentTimeMillis();
			
			if (index instanceof SurrogateSpaceIndex && optimise) {
				double surrogateRadius = parameters.getDouble("surrogateRadius", radius);
				SurrogateSpaceIndex ssindex = (SurrogateSpaceIndex)index;
				
				for (int i = 0; i < count; i++) {
	                resultCount += ssindex.search(i, radius, surrogateRadius).size();
	                progress.incrementDone();
	            }
			}
			else if (optimise) {
				for (int i = 0; i < count; i++) {
	                resultCount += index.search(i, radius).size();
	                progress.incrementDone();
	            }
			}
			else {
				for (int i = 0; i < count; i++) {
					Descriptor descriptor = objects.get(i);
	                List<SearchResult> results = index.search(descriptor, radius);
	                resultCount += results.size();
	                
	                if (clusters != null) {
	                	Set<Integer> cluster = clusters.get(i);
	                	
	                	if (cluster != null) {
		                	for (SearchResult result: results) {
		                		if (!cluster.contains(result.getResult()))
		                			falsePositiveCount++;
		                	}
	                	}
	                	else {
	                		falsePositiveCount += results.size();
	                	}
	                }
	                
	                progress.incrementDone();
	            }
			}
			
			time = System.currentTimeMillis() - time;
			
            index.close();
            reporter.stop();
            System.out.printf("Average time: %.0f ms\n", (double)time / count);
            System.out.printf("Average calculations: %.0f\n", (double)index.getNumberOfDistanceCalculations() / count);
            
            if (index instanceof SurrogateSpaceIndex)
            	System.out.printf("Average surrogate calculations: %.0f\n", 
            		(double)((SurrogateSpaceIndex)index).getNumberOfSurrogateDistanceCalculations() / count);
            
            System.out.printf("Number of results: %d\n", resultCount);
            
            if (clusters != null)
            	System.out.printf("Average false positives: %.0f\n", (double)falsePositiveCount / count);
            
            if (parameters.getBoolean("csv", false)) {
            	System.err.printf("%.0f,%.0f,%d,%.0f\n", (double)time / count,
            		(double)index.getNumberOfDistanceCalculations() / count, resultCount,
            		(double)falsePositiveCount / count);
            }
		}
		catch (ParameterException | IOException | FileFormatException ex) {
			reporter.stop();
			System.out.println(ex.getMessage());
		}
	}

	@Override
	public String getName() {
		return "SearchTime";
	}

	@Override
	public String describe() {
		parameters.describe("radius", "The search radius to use.");
		parameters.describe("count", "The number of searches to make to calculate the average (default 1000).");
		parameters.describe("optimise", "Set to true to search with optimisation.");
		parameters.describe("surrogateRadius", "For surrogate indices: the radius to use when searching the surrogate "
				+ "index (default same as radius, only works if optimise=true).");
		parameters.describe("clusters", "A file containing pairs of points which have been judged to be near duplicates.");
		parameters.describe("csv", "Set to true to have summarised information printed to stderr in CSV format.");
		indexOpener.describe();
		return "Performs a number of searches against an index and calculates the average search time.";
	}
}
