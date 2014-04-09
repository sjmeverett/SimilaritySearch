package ndi.commands.calculations;

import java.io.IOException;

import metricspaces.descriptors.Descriptor;
import metricspaces.files.DescriptorFile;
import metricspaces.metrics.Metric;
import ndi.MetricLoader;
import ndi.files.DescriptorFileLoader;

import commandline.Command;
import commandline.ParameterException;
import commandline.Parameters;

public class GetDistanceCommand implements Command {
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
		try {
			String path = parameters.require("descriptors");
			DescriptorFile objects = loader.load(path);
			Metric metric = metrics.getMetric(objects.getHeader());

			Descriptor x = objects.get(parameters.getInt("x"));
			Descriptor y = objects.get(parameters.getInt("y"));
			
			System.out.printf("Distance: %.3f\n", metric.getDistance(x, y));
		}
		catch (ParameterException e) {
			System.out.println(e.getMessage());
		}
		catch (IOException e) {
			System.out.println("IO error: " + e.getMessage());
		}
	}

	@Override
	public String getName() {
		return "GetDistance";
	}

	@Override
	public String describe() {
		parameters.describe("descriptors", "The path to the descriptor file to run the distance caluclations over.");
		parameters.describe("x", "The ID of one of the pair.");
		parameters.describe("y", "The ID of the other of the pair.");
		metrics.describe();
		return "Calculates the distance between two objects using th specified distance function.";
	}
	
	
}