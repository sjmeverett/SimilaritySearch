package ndi.commands.calculations;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import metricspaces.Progress;
import metricspaces.files.DescriptorFile;
import metricspaces.indexes.Index;
import metricspaces.metrics.Metric;
import ndi.ANMRRCalculator;
import ndi.MetricLoader;
import ndi.files.ClusterReader;
import ndi.files.DescriptorFileLoader;
import ndi.files.FileFormatException;
import ndi.files.IndexFileLoader;

import commandline.Command;
import commandline.ParameterException;
import commandline.Parameters;
import commandline.ProgressReporter;

public class CalculateANMRRCommand implements Command {
	private Parameters parameters;
	private DescriptorFileLoader descriptorLoader;
	private IndexFileLoader indexLoader;
	private MetricLoader metricLoader;
	
	@Override
	public void init(Parameters parameters) {
		this.parameters = parameters;
		descriptorLoader = new DescriptorFileLoader(parameters);
		indexLoader = new IndexFileLoader(parameters);
		metricLoader = new MetricLoader(parameters);
	}

	@Override
	public void run() {
		Progress progress = new Progress();
		ProgressReporter reporter = new ProgressReporter(progress, 250);
		
		try {
			//load the clusters
			ClusterReader reader = new ClusterReader(parameters.require("input"), false);
			List<Set<Integer>> clusters = new ArrayList<>(reader.read().values());
			
			if (parameters.get("max") != null) {
				clusters = clusters.subList(0, parameters.getInt("max"));
			}
			
			ANMRRCalculator calculator = new ANMRRCalculator(clusters, progress);
			double anmrr = 0;
			
			if (parameters.get("index") != null) {
				Index index = indexLoader.load(parameters.require("index"), progress);
				double initialRadius = parameters.getDouble("initialRadius");
				double increasingFactor = parameters.getDouble("increasingFactor", 1.1);
				anmrr = calculator.calculate(index, initialRadius, increasingFactor);
			}
			else {
				DescriptorFile objects = descriptorLoader.load(parameters.require("objects"));
				Metric metric = metricLoader.getMetric(objects.getHeader());
				anmrr = calculator.calculate(objects, metric);
			}
			
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
		parameters.describe("index", "The path to the index file to use.");
		parameters.describe("initialRadius", "The initial search radius to use.");
		parameters.describe("increasingFactor", "The amount to increase the search radius by each iteration ("
				+ "default 1.1).");
		parameters.describe("input", "A CSV file containing a list of pairs of near-duplicates.");
		parameters.describe("objects", "The descriptor file to search.");
		parameters.describe("max", "The maximum number of clusters to assess (by default all the clusters are assessed).");
		metricLoader.describe();
		
		return "Calculates the MPEG-7 ANMRR statistic for the given search strategy and list of near-duplicates.  Either "
				+ "an index file, initial radius, and increasing factor should be provided or a descriptor file and metric. "
				+ "The former option should be much faster, but the latter option should be used for non-metric distance "
				+ "functions.";
	}

}
