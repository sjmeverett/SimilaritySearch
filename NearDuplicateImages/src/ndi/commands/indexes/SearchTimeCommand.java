package ndi.commands.indexes;

import java.io.IOException;

import metricspaces.Progress;
import metricspaces.descriptors.Descriptor;
import metricspaces.files.DescriptorFile;
import metricspaces.indexes.Index;
import metricspaces.indexes.PivotedList;
import ndi.files.IndexFileLoader;

import commandline.Command;
import commandline.ParameterException;
import commandline.Parameters;
import commandline.ProgressReporter;

public class SearchTimeCommand implements Command {
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
			Index<Integer, Descriptor> index = indexLoader.load(parameters.require("index"), progress);
			DescriptorFile<Integer, Descriptor> objects = index.getObjects();
			
			double radius = parameters.getDouble("radius", Double.NaN);
			int count = parameters.getInt("count", 1000);
			long resultCount = 0;
			
			progress.setOperation("Searching", count);
			
			long time = System.currentTimeMillis();
			
			if (index instanceof PivotedList) {
				PivotedList<Integer, Descriptor> plindex = (PivotedList<Integer, Descriptor>)index;
				
				for (int i = 0; i < count; i++) {
					Descriptor query = objects.get(i).getDescriptor();
					double[] queryList = plindex.getDistanceList(i);
					resultCount += plindex.search(query, queryList, radius).size();
					progress.incrementDone();
				}
			}
			else {
				for (int i = 0; i < count; i++) {
	                Descriptor query = objects.get(i).getDescriptor();
	                resultCount += index.search(query, radius).size();
	                progress.incrementDone();
	            }
			}
			
			time = System.currentTimeMillis() - time;
			
            index.close();
            reporter.stop();
            System.out.printf("Average time: %.0f ms\n", (double)time / count);
            System.out.printf("Average calculations: %.0f\n", (double)index.getNumberOfDistanceCalculations() / count);
            System.out.printf("Number of results: %d\n", resultCount);
		}
		catch (ParameterException | IOException ex) {
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
		parameters.describe("index", "The index file.");
		parameters.describe("radius", "The search radius to use.");
		parameters.describe("count", "The number of searches to make to calculate the average (default 1000).");
		
		return "Performs a number of searches against an index and calculates the average search time.";
	}
}
