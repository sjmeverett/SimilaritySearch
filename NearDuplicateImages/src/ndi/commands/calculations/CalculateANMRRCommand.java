package ndi.commands.calculations;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import metricspaces.Progress;
import metricspaces.files.DescriptorFile;
import metricspaces.metrics.Metric;
import ndi.MetricLoader;
import ndi.Mpeg7RetrievalRank;
import ndi.files.ClusterReader;
import ndi.files.DescriptorFileLoader;
import ndi.files.FileFormatException;

import commandline.Command;
import commandline.ParameterException;
import commandline.Parameters;
import commandline.ProgressReporter;

public class CalculateANMRRCommand implements Command {
	private Parameters parameters;
	private DescriptorFileLoader loader;
	private MetricLoader metrics;
	
	@Override
	public void init(Parameters parameters) {
		this.parameters = parameters;
		loader = new DescriptorFileLoader(parameters);
		metrics = new MetricLoader(parameters);
	}

	@Override
	public void run() {
		Progress progress = new Progress();
		ProgressReporter reporter = new ProgressReporter(progress, 250);
		
		try {
			DescriptorFile objects = loader.load(parameters.require("objects"));
			Metric metric = metrics.getMetric(objects.getHeader());
			
			//load the clusters
			ClusterReader reader = new ClusterReader(parameters.require("input"), false);
			List<Set<Integer>> clusters = new ArrayList<>(reader.read().values());
			
			//calculate the ANMRR
			Mpeg7RetrievalRank rr = new Mpeg7RetrievalRank(clusters, progress);
			double anmrr = rr.getANMRR(objects, metric, parameters.getInt("max", Integer.MAX_VALUE));
			
			reporter.stop();
			
			System.out.printf("ANMRR: %f\n", anmrr);
		}
		catch (ParameterException | IOException | FileFormatException e) {
			reporter.stop();
			System.out.println(e.getMessage());
		}
	}
	

	@Override
	public String getName() {
		return "CalculateANMRR";
	}

	@Override
	public String describe() {
		parameters.describe("input", "A CSV file containing a list of pairs of near-duplicates.");
		parameters.describe("objects", "The descriptor file to search.");
		parameters.describe("max", "The maximum number of images to assess (by default all the images in the "
				+ "file are assessed).");
		metrics.describe();
		return "Calculates the MPEG-7 ANMRR statistic for the given search strategy and list of near-duplicates.";
	}

}
