package ndi.commands.indexes;

import java.io.IOException;
import java.util.List;

import metricspaces.Progress;
import metricspaces.descriptors.Descriptor;
import metricspaces.files.DescriptorFile;
import metricspaces.indexes.Index;
import metricspaces.indexes.SearchResult;
import ndi.files.IndexFileLoader;
import ndi.files.PairDistanceWriter;

import commandline.Command;
import commandline.ParameterException;
import commandline.Parameters;
import commandline.ProgressReporter;

public class SearchAllCommand implements Command {
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
			PairDistanceWriter writer = new PairDistanceWriter(parameters.require("output"));
			double radius = parameters.getDouble("radius", Double.NaN);
			
			int count = objects.getCapacity();
			int found = 0;
			
			progress.setOperation("Searching", count);
			
			for (int i = 0; i < count; i++) {
                Descriptor query = objects.get(i).getDescriptor();

                List<SearchResult<Integer>> results = index.search(query, radius);

                for (SearchResult<Integer> result: results) {
                    if (result.getResult() != i) {
                        writer.write(i, result.getResult(), result.getDistance());
                        found++;
                    }
                }

                progress.incrementDone();
            }

			writer.close();
            index.close();
            reporter.stop();
            System.out.printf("Found %d pairs.\n", found);
		}
		catch (ParameterException | IOException ex) {
			reporter.stop();
			System.out.println(ex.getMessage());
		}
	}

	@Override
	public String getName() {
		return "SearchAll";
	}

	@Override
	public String describe() {
		parameters.describe("index", "The index file.");
		parameters.describe("radius", "The search radius to use.");
		parameters.describe("output", "The path to the CSV file to output to.");
		
		return "Searches the given index with all the objects present in the index and writes out the results to a"
				+ "CSV file";
	}
}
