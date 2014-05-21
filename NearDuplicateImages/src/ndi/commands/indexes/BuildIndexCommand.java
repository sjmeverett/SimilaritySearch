package ndi.commands.indexes;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import metricspaces.Progress;
import metricspaces.files.DescriptorFile;
import metricspaces.files.DescriptorFileHeader;
import metricspaces.indexes.Index;
import metricspaces.metrics.Metric;
import ndi.MetricLoader;
import ndi.files.IdFileReader;
import ndi.files.IndexFileCreator;

import commandline.Command;
import commandline.ParameterException;
import commandline.Parameters;
import commandline.ProgressReporter;

public class BuildIndexCommand implements Command {
	private Parameters parameters;
	private IndexFileCreator indexCreator;
	private MetricLoader metricLoader;
	
	@Override
	public void init(Parameters parameters) {
		this.parameters = parameters;
		indexCreator = new IndexFileCreator(parameters);
		metricLoader = new MetricLoader(parameters);
	}

	@Override
	public void run() {
		Progress progress = new Progress();
		ProgressReporter reporter = new ProgressReporter(progress, 250);
		
		try {
			String descriptorPath = parameters.require("objects");
			String indexPath = parameters.require("output");
			
			DescriptorFile objects = DescriptorFileHeader.open(descriptorPath);
			String idsPath = parameters.get("ids");
			List<Integer> ids;
			
			if (idsPath != null) {
				IdFileReader reader = new IdFileReader(idsPath);
				ids = reader.read();
			}
			else {
				ids = new ArrayList<>(objects.getCapacity());
				
				for (int i = 0; i < objects.getCapacity(); i++)
					ids.add(i);
			}
			
			Metric metric = metricLoader.getMetric(objects.getHeader());
			Index index = indexCreator.create(indexPath, objects, metric, ids.size(), progress);
			
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
		indexCreator.describe();
		metricLoader.describe();
		return "Builds a new index file.";
	}

}
