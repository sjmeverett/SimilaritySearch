package ndi.commands.calculations;

import java.io.IOException;

import metricspaces.Progress;
import metricspaces.RandomHelper;
import metricspaces.descriptors.Descriptor;
import metricspaces.files.DescriptorFile;
import metricspaces.files.DescriptorFileHeader;
import metricspaces.metrics.Metric;
import ndi.MetricLoader;

import commandline.Command;
import commandline.ParameterException;
import commandline.Parameters;
import commandline.ProgressReporter;

public class TimeMetricCommand implements Command {
	private Parameters parameters;
	private MetricLoader metrics;

	@Override
	public void init(Parameters parameters) {
		this.parameters = parameters;
		metrics = new MetricLoader(parameters);
	}

	@Override
	public void run() {
		Progress progress = new Progress();
		ProgressReporter reporter = new ProgressReporter(progress, 250);
		
		try {
			String path = parameters.require("descriptors");
			DescriptorFile objects = DescriptorFileHeader.open(path);
			Metric metric = metrics.getMetric(objects.getHeader());
			
			int count = parameters.getInt("count", 1000);
			int n = objects.getCapacity() - 1;
			
			progress.setOperation("Timing", count);
			long time = System.currentTimeMillis();
			
			for (int i = 0; i < count; i++) {
				Descriptor x = objects.get(RandomHelper.getNextInt(0, n));
				Descriptor y = objects.get(RandomHelper.getNextInt(0, n));
				metric.getDistance(x, y);
				progress.incrementDone();
			}
			
			time = System.currentTimeMillis() - time;
			reporter.stop();
			
			System.out.printf("Average time: %.3f ms\n", (double)time / count);
		}
		catch (ParameterException e) {
			reporter.stop();
			System.out.println(e.getMessage());
		}
		catch (IOException e) {
			reporter.stop();
			System.out.println("IO error: " + e.getMessage());
		}
	}

	@Override
	public String getName() {
		return "TimeMetric";
	}

	@Override
	public String describe() {
		parameters.describe("descriptors", "The path to the descriptor file to run the distance caluclations over.");
		parameters.describe("count", "The number of distance calculations to average over (default 1000).");
		metrics.describe();
		return "Runs a number of distance calculations using a specified metric and calculates the average time taken.";
	}
	
	
}
