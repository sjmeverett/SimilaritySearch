package ndi.commands.indexes;

import java.io.IOException;
import java.util.List;

import metricspaces.Progress;
import metricspaces.descriptors.Descriptor;
import metricspaces.files.DescriptorFile;
import metricspaces.indexes.Index;
import metricspaces.indexes.SearchResult;
import ndi.files.IndexFileOpener;
import ndi.files.PairDistanceWriter;

import commandline.Command;
import commandline.ParameterException;
import commandline.Parameters;
import commandline.ProgressReporter;

public class SearchAllCommand implements Command {
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
			PairDistanceWriter writer = new PairDistanceWriter(parameters.require("output"));
			double radius = parameters.getDouble("radius", Double.NaN);
			
			int count = parameters.getInt("count", objects.getCapacity());
			int found = 0;
			
			progress.setOperation("Searching", count);
			
			for (int i = 0; i < count; i++) {
                Descriptor query = objects.get(i);

                List<SearchResult> results = index.search(query, radius);

                for (SearchResult result: results) {
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
		parameters.describe("radius", "The search radius to use.");
		parameters.describe("output", "The path to the CSV file to output to.");
		parameters.describe("count", "The number of points to search (default, all of them).");
		indexOpener.describe();
		
		return "Searches the given index with all the objects present in the index and writes out the results to a"
				+ "CSV file";
	}
}
