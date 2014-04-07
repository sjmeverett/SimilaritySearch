package ndi.commands.indexes;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import metricspaces.PairDistance;
import metricspaces.Progress;
import metricspaces.descriptors.Descriptor;
import metricspaces.descriptors.ObjectWithDescriptor;
import metricspaces.files.DescriptorFile;
import metricspaces.indexes.Index;
import metricspaces.indexes.PivotedList;
import metricspaces.indexes.SearchResult;
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
			Index<Integer, Descriptor> _index = indexLoader.load(parameters.require("index"), progress);
			DescriptorFile<Integer, Descriptor> objects = _index.getObjects();
			double initialRadius = parameters.getDouble("initialRadius");
			double increasingFactor = parameters.getDouble("increasingFactor", 1.1);
			int numberOfPairs = parameters.getInt("pairs", 5000);
			
			//require the index to be a pivoted list so we can make some time savings
			if (!(_index instanceof PivotedList))
				throw new ParameterException("Index must be a pivoted list index.");
			
			PivotedList<Integer, Descriptor> index = (PivotedList<Integer, Descriptor>)_index;
			
			//make a list of query indices - initially starts with all of them
			Set<Integer> queries = new HashSet<>();
			List<Integer> toRemove = new ArrayList<>();
			for (int i = 0; i < objects.getCapacity(); i++) queries.add(i);
			
			List<PairDistance<Integer>> results = new ArrayList<>();
			double radius = initialRadius;
			
			//keep looping until the required number of pairs have been retrieved
			while (results.size() < numberOfPairs) {
				//search for each of the remaining queries
				for (Integer i: queries) {
					ObjectWithDescriptor<Integer, Descriptor> query = objects.get(i);
					double[] queryList = index.getDistanceList(i);
					SearchResult<Integer> result = index.findNearestNeighbour(query.getDescriptor(), queryList, i, radius);
					
					//we've got a nearest neighbour - add it to the list
					//and schedule the two parts of the pair for deletion
					if (result != null) {
						results.add(new PairDistance<Integer>(query.getObject(), result.getResult(), result.getDistance()));
						toRemove.add(i);
						toRemove.add(result.getResultIndex());
					}
				}
				
				queries.removeAll(toRemove);
				toRemove.clear();
				radius *= increasingFactor;
			}
			
			index.close();
			
			List<PairDistance<Integer>> list = new ArrayList<>(results);
			Collections.sort(list);
			
			PairDistanceWriter writer = new PairDistanceWriter(parameters.require("output"));
			writer.writeAll(list);
			writer.close();
			
            reporter.stop();
            System.out.printf("Found %d pairs.\n", list.size());
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
