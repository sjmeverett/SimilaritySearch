package ndi.commands.calculations;

import java.io.IOException;

import metricspaces.descriptors.DescriptorFile;
import metricspaces.descriptors.DescriptorFileFactory;
import metricspaces.metrics.MetricSpace;
import commandline.Command;
import commandline.ParameterException;
import commandline.Parameters;

public class GetDistanceCommand implements Command {
	private Parameters parameters;

	@Override
	public void init(Parameters parameters) {
		this.parameters = parameters;
	}

	@Override
	public void run() {
		try {
			String path = parameters.require("descriptors");
			DescriptorFile objects = DescriptorFileFactory.open(path, false);
			MetricSpace space = objects.getMetricSpace(parameters.require("metric"));

			System.out.printf("Distance: %f\n", space.getDistance(parameters.getInt("x"), parameters.getInt("y")));
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
		parameters.describe("metric", "The metric to use.");
		return "Calculates the distance between two objects using th specified distance function.";
	}
	
	
}