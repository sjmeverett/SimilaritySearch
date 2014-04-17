package ndi.commands.indexes;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Queue;
import java.util.Set;

import metricspaces.PairDistance;
import metricspaces.Progress;
import metricspaces.indexes.ExpandingSearch;
import metricspaces.indexes.Index;
import metricspaces.indexes.SearchResult;
import ndi.FixedSizePriorityQueue;
import ndi.files.IndexFileLoader;
import ndi.files.PairDistanceWriter;
import commandline.Command;
import commandline.ParameterException;
import commandline.Parameters;
import commandline.ProgressReporter;

public class FindClosestPairsCommand implements Command {
	private Parameters parameters;
	private IndexFileLoader indexLoader;

	@Override
	public void init(Parameters parameters) {
		this.parameters = parameters;
		indexLoader = new IndexFileLoader(parameters);
	}

	@Override
	public void run() {
		Progress progress = new Progress();
		ProgressReporter reporter = new ProgressReporter(progress,  250);
		
		try {
			//get the parameters
			Index index = indexLoader.load(parameters.require("index"), progress);
			double initialRadius = parameters.getDouble("initialRadius");
			double increasingFactor = parameters.getDouble("increasingFactor", 1.1);
			int numberOfPairs = parameters.getInt("pairs", 5000);
			
			ExpandingSearch search = new ExpandingSearch(index, initialRadius, increasingFactor);
			Queue<SearchResult> pairs = new FixedSizePriorityQueue<SearchResult>(numberOfPairs, null);
			
			progress.setOperation("Finding pairs", numberOfPairs);
			
			while (search.hasQueries() && pairs.size() < numberOfPairs) {
				Iterator<List<SearchResult>> it = search.search();
				
				while(it.hasNext()) { 
					List<SearchResult> results = it.next();
					Collections.sort(results);
					
					//make sure we have some results
					if (results.size() == 0) continue;
					SearchResult result = results.get(0);
					
					//skip the result containing the query itself
					if (result.getQuery() == result.getResult()) {
						if (results.size() == 1) continue;
						result = results.get(1);
					}
					
					//add to results
					pairs.add(result);
					it.remove();
					progress.incrementDone();
				}
			}
			
			index.close();
			
			PairDistanceWriter writer = new PairDistanceWriter(parameters.require("output"));
			writer.writeAllResults(pairs);
			writer.close();
			
            reporter.stop();
            System.out.printf("Found %d pairs.\n", pairs.size());
		}
		catch (ParameterException | IOException ex) {
			reporter.stop();
			System.out.println(ex.getMessage());
		}
	}
	
	
	@Override
	public String getName() {
		return "FindClosestPairs";
	}

	@Override
	public String describe() {
		parameters.describe("index", "The index file.");
		parameters.describe("initialRadius", "The initial search radius to use.");
		parameters.describe("increasingFactor", "The amount to increase the search radius by each iteration ("
				+ "default 1.1).");
		parameters.describe("pairs", "The minimum number of pairs to find (default 5000).");
		parameters.describe("output", "The path to the CSV file to output to.");
		
		return "Searches the given index with all the objects present in the index in order to find the n closest pairs. "
				+ "The search uses initialRadius as the search radius, and iterates increasing the radius by "
				+ "increasingFactor each time until the minimum number of pairs have been found.";
	}
}
