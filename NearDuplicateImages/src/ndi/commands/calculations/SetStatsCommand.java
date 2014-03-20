package ndi.commands.calculations;

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

public class SetStatsCommand implements Command {
	private Parameters parameters;
	private DescriptorFileLoader descriptorLoader;
	private MetricLoader metricLoader;
	
	private static final int MEAN_COUNT = 10000;
	
	@Override
	public void init(Parameters parameters) {
		this.parameters = parameters;
		descriptorLoader = new DescriptorFileLoader(parameters);
		metricLoader = new MetricLoader(parameters);
	}

	@Override
	public void run() {
		Progress progress = new Progress();
		ProgressReporter reporter = new ProgressReporter(progress, 250);
		
		try {
			DescriptorFile<Integer, Descriptor> objects = descriptorLoader.load(parameters.require("file"));
			Metric<Descriptor> metric = metricLoader.getMetric(objects.getHeader());
			
			int n = MEAN_COUNT * (MEAN_COUNT - 1) / 2;
			double sum = 0;
			
			progress.setOperation("Calculating mean distance", n);
			
			for (int i = 0; i < MEAN_COUNT; i++) {
				Descriptor x = objects.get(i).getDescriptor();
				
				for (int j = i + 1; j < MEAN_COUNT; j++) {
					Descriptor y = objects.get(j).getDescriptor();
					sum += metric.getDistance(x, y);
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
		metricLoader.describe();
		return "Calculates the mean distance for the specified descriptor file and metric.";
	}

}
