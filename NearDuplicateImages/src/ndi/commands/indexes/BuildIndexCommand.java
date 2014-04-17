package ndi.commands.indexes;

import java.io.IOException;
import java.util.List;

import metricspaces.Progress;
import metricspaces.files.DescriptorFile;
import metricspaces.indexes.Index;
import metricspaces.metrics.Metric;
import ndi.MetricLoader;
import ndi.files.DescriptorFileLoader;
import ndi.files.IdFileReader;
import ndi.files.IndexFileLoader;

import commandline.Command;
import commandline.ParameterException;
import commandline.Parameters;
import commandline.ProgressReporter;

public class BuildIndexCommand implements Command {
	private Parameters parameters;
	private IndexFileLoader indexLoader;
	private DescriptorFileLoader descriptorLoader;
	private MetricLoader metricLoader;
	
	@Override
	public void init(Parameters parameters) {
		this.parameters = parameters;
		indexLoader = new IndexFileLoader(parameters);
		descriptorLoader = new DescriptorFileLoader(parameters);
		metricLoader = new MetricLoader(parameters);
	}

	@Override
	public void run() {
		Progress progress = new Progress();
		ProgressReporter reporter = new ProgressReporter(progress, 250);
		
		try {
			String descriptorPath = parameters.require("objects");
			String indexPath = parameters.require("output");
			String idsPath = parameters.require("ids");
			IdFileReader reader = new IdFileReader(idsPath);
			List<Integer> ids = reader.read();
			
			DescriptorFile objects = descriptorLoader.load(descriptorPath);
			Metric metric = metricLoader.getMetric(objects.getHeader());
			Index index = indexLoader.create(indexPath, objects, metric, ids.size(), progress);
			
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
		parameters.describe("ids", "The path to the file containing the list of IDs to store in the index.");
		indexLoader.describe();
		metricLoader.describe();
		return "Builds a new index file.";
	}

}
