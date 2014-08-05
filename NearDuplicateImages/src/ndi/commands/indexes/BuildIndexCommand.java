package ndi.commands.indexes;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import metricspaces.Progress;
import metricspaces.update.indices.Index;
import metricspaces.update.indices.VantagePointTreeIndex;
import ndi.files.IdFileReader;

import commandline.Command;
import commandline.ParameterException;
import commandline.Parameters;
import commandline.ProgressReporter;

public class BuildIndexCommand implements Command {
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
			//create the new index file
			String indexImplementation = parameters.require("indexImplementation");
			String indexPath = parameters.require("output");
			String descriptorPath = parameters.require("objects");
			String metricName = parameters.require("metric");
			int count = parameters.getInt("count", -1);
			Index index;
			
			if (indexImplementation.equals("VP")) {
				index = new VantagePointTreeIndex(indexPath, count, descriptorPath, metricName, progress);
			}
			else {
				throw new ParameterException("unrecognised index implementation");
			}
			
			//determine which descriptors to add to the index
			String idsPath = parameters.get("ids");
			List<Integer> ids;
			
			if (idsPath != null) {
				IdFileReader reader = new IdFileReader(idsPath);
				ids = reader.read();
			}
			else {
				if (count == -1) count = index.getDescriptors().getSize();
				ids = new ArrayList<>(count);
				
				for (int i = 0; i < count; i++)
					ids.add(i);
			}
			
			//build the index
			index.build(ids);
			index.close();
			reporter.stop();
		}
		catch (ParameterException | IOException ex) {
			reporter.stop();
			System.out.println(ex.getMessage());
		}
	}

	@Override
	public String getName() {
		return "BuildIndex";
	}

	@Override
	public String describe() {
		parameters.describe("objects", "The descriptor file to build the index from.");
		parameters.describe("output", "The path to write the index file to.");
		parameters.describe("ids", "The path to the file containing the list of IDs to store in the index.  Omit to use all "
				+ "of the objects in the descriptor file.");
		parameters.describe("count", "Use this parameter to include a fixed number of points in the index.");
		parameters.describe("metric", "The metric to use.");
		parameters.describe("indexImplementation", "The index implentation to use.");
		return "Builds a new index file.";
	}

}
