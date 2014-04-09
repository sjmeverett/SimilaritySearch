package ndi.commands.calculations;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

import metricspaces.Progress;
import metricspaces.descriptors.Descriptor;
import metricspaces.files.DescriptorFile;
import metricspaces.metrics.Metric;
import ndi.MetricLoader;
import ndi.files.DescriptorFileLoader;

import commandline.Command;
import commandline.ParameterException;
import commandline.Parameters;
import commandline.ProgressReporter;

public class WriteDistancesCommand implements Command {
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
			int count = parameters.getInt("count", 1000);
			
			BufferedWriter writer = new BufferedWriter(new FileWriter(parameters.require("output")));
			progress.setOperation("Calculating distances", count * (count - 1) / 2);
			
			for (int i = 0; i < count; i++) {
				Descriptor x = objects.get(i);
				
				for (int j = i + 1; j < count; j++) {
					Descriptor y = objects.get(j);
					double distance = metric.getDistance(x, y);
					
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
		parameters.describe("count", "The number of objects to calculate the distance among.");
		parameters.describe("output", "The path to the file to output the distances to.");
		metrics.describe();
		return "Calculates the distance between all the pairs in a specified number of objects and writes "
				+ "the distances to a file.";
	}

}
