package ndi.commands.calculations;

import java.io.IOException;

import metricspaces.descriptors.DescriptorFile;
import metricspaces.descriptors.DescriptorFileFactory;
import metricspaces.metrics.MetricSpace;
import metricspaces.util.Progress;
import metricspaces.util.RandomHelper;
import commandline.Command;
import commandline.ParameterException;
import commandline.Parameters;
import commandline.ProgressReporter;

public class TimeMetricCommand implements Command {
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
			String path = parameters.require("descriptors");
			DescriptorFile objects = DescriptorFileFactory.open(path, false);
			MetricSpace space = objects.getMetricSpace(parameters.require("metric"));
			
			int count = parameters.getInt("count", 1000);
			int n = objects.getSize() - 1;
			
			progress.setOperation("Timing", count);
			long time = System.currentTimeMillis();
			
			for (int i = 0; i < count; i++) {
				space.getDistance(RandomHelper.getNextInt(0, n), RandomHelper.getNextInt(0, n));
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
		parameters.describe("metric", "The metric to use.");
		return "Runs a number of distance calculations using a specified metric and calculates the average time taken.";
	}
	
	
}
