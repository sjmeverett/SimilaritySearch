package ndi.commands.indexes;

import java.io.IOException;
import java.util.List;

import metricspaces.Progress;
import metricspaces.indexes.SearchResult;
import metricspaces.update.indices.Index;
import metricspaces.update.indices.IndexFactory;

import commandline.Command;
import commandline.ParameterException;
import commandline.Parameters;
import commandline.ProgressReporter;

public class SearchCommand implements Command {
	private Parameters parameters;
	
	@Override
	public void init(Parameters parameters) {
		this.parameters = parameters;
	}

	@Override
	public void run() {
		Progress progress = new Progress();
		ProgressReporter reporter = new ProgressReporter(progress, 250);
		
		try {
			Index index = IndexFactory.open(parameters.require("index"), false, progress);
			double radius = parameters.getDouble("radius", Double.NaN);
			
			List<SearchResult> results = index.search(parameters.getInt("query"), radius);
			
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
		parameters.describe("index", "The index to use.");
		return "Searches the given index for the specified image and returns the results.";
	}
}
