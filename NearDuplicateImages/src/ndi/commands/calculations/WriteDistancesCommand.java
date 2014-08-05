package ndi.commands.calculations;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

import metricspaces.descriptors.DescriptorFile;
import metricspaces.descriptors.DescriptorFileFactory;
import metricspaces.metrics.MetricSpace;
import metricspaces.metrics.MetricSpaceObject;
import metricspaces.util.Progress;
import commandline.Command;
import commandline.ParameterException;
import commandline.Parameters;
import commandline.ProgressReporter;

public class WriteDistancesCommand implements Command {
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
			DescriptorFile objects = DescriptorFileFactory.open(parameters.require("objects"), false);
			MetricSpace space = objects.getMetricSpace(parameters.require("metric"));
			int count = parameters.getInt("count", 1000);
			
			BufferedWriter writer = new BufferedWriter(new FileWriter(parameters.require("output")));
			progress.setOperation("Calculating distances", count * (count - 1) / 2);
			
			for (int i = 0; i < count; i++) {
				MetricSpaceObject obj = space.getObject(i);
				
				for (int j = i + 1; j < count; j++) {
					double distance = obj.getDistance(j);
					
					writer.write(String.format("%f\n", distance));
					progress.incrementDone();
				}
			}
			
			writer.close();
			reporter.stop();
		}
		catch (IOException e) {
			reporter.stop();
			System.out.println("IO error: " + e.getMessage());
		}
		catch (ParameterException e) {
			reporter.stop();
			System.out.println(e.getMessage());
		}
	}

	@Override
	public String getName() {
		return "WriteDistances";
	}

	@Override
	public String describe() {
		parameters.describe("objects", "The path to the descriptor file.");
		parameters.describe("count", "The number of objects to calculate the distance among (default 1000).");
		parameters.describe("output", "The path to the file to output the distances to.");
		parameters.describe("metric", "The metric to use.");
		return "Calculates the distance between all the pairs in a specified number of objects and writes "
				+ "the distances to a file.";
	}

}
