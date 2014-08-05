package ndi.commands.calculations;

import java.io.IOException;

import metricspaces.Progress;
import metricspaces.update.common.DescriptorFile;
import metricspaces.update.common.DescriptorFileFactory;
import metricspaces.update.common.MetricSpace;
import metricspaces.update.common.MetricSpaceObject;

import commandline.Command;
import commandline.ParameterException;
import commandline.Parameters;
import commandline.ProgressReporter;

public class GetMeanDistanceCommand implements Command {
	private Parameters parameters;
	
	private static final int MEAN_COUNT = 10000;
	
	@Override
	public void init(Parameters parameters) {
		this.parameters = parameters;
	}

	@Override
	public void run() {
		Progress progress = new Progress();
		ProgressReporter reporter = new ProgressReporter(progress, 250);
		
		try {
			DescriptorFile objects = DescriptorFileFactory.open(parameters.require("file"), false);
			MetricSpace space = objects.getMetricSpace(parameters.require("metric"));
			
			int n = MEAN_COUNT * (MEAN_COUNT - 1) / 2;
			double sum = 0;
			
			progress.setOperation("Calculating mean distance", n);
			
			for (int i = 0; i < MEAN_COUNT; i++) {
				MetricSpaceObject obj = space.getObject(i);
				
				for (int j = i + 1; j < MEAN_COUNT; j++) {
					sum += obj.getDistance(j);
					progress.incrementDone();
				}
			}
			
			System.out.printf("Average distance: %f\n", sum / n);
			reporter.stop();
		}
		catch (ParameterException | IOException ex) {
			reporter.stop();
			System.out.println(ex.getMessage());
		}
	}
	

	@Override
	public String getName() {
		return "GetMeanDistance";
	}

	@Override
	public String describe() {
		parameters.describe("file", "The path to the descriptor file.");
		parameters.describe("metric", "The metric to use.");
		return "Calculates the mean distance for the specified descriptor file and metric.";
	}

}
