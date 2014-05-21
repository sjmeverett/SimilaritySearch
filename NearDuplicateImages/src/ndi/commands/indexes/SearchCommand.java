package ndi.commands.indexes;

import java.io.IOException;
import java.util.List;

import metricspaces.Progress;
import metricspaces.descriptors.Descriptor;
import metricspaces.files.DescriptorFile;
import metricspaces.indexes.Index;
import metricspaces.indexes.SearchResult;
import ndi.files.IndexFileOpener;

import commandline.Command;
import commandline.ParameterException;
import commandline.Parameters;
import commandline.ProgressReporter;

public class SearchCommand implements Command {
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
		ProgressReporter reporter = new ProgressReporter(progress, 250);
		
		try {
			Index index = indexOpener.open(progress);
			DescriptorFile objects = index.getObjects();
			double radius = parameters.getDouble("radius", Double.NaN);
			
			Descriptor query = objects.get(parameters.getInt("query"));
			List<SearchResult> results = index.search(query, radius);
			
			reporter.stop();
			System.out.println("Found " + results.size() + " results:");
			
			for (SearchResult result: results)
				System.out.println(result);
		}
		catch (ParameterException | IOException ex) {
			reporter.stop();
			System.out.println(ex.getMessage());
		}
	}

	@Override
	public String getName() {
		return "Search";
	}

	@Override
	public String describe() {
		parameters.describe("radius", "The search radius to use.");
		parameters.describe("query", "The ID of the image to search for.");
		indexOpener.describe();
		return "Searches the given index for the specified image and returns the results.";
	}
}
