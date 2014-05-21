package ndi.commands.calculations;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import metricspaces.Progress;
import metricspaces.descriptors.Descriptor;
import metricspaces.descriptors.DivergenceCalculator;
import metricspaces.files.DescriptorFile;
import metricspaces.files.DescriptorFileHeader;
import metricspaces.indexes.pivotselectors.PivotSelector;
import metricspaces.metrics.Metric;
import metricspaces.metrics.Metrics;
import ndi.PivotSelectorLoader;

import commandline.Command;
import commandline.ParameterException;
import commandline.Parameters;
import commandline.ProgressReporter;

public class DivergenceCommand implements Command {
	private Parameters parameters;
	private PivotSelectorLoader pivotSelectorLoader;
	
	@Override
	public void init(Parameters parameters) {
		this.parameters = parameters;
		pivotSelectorLoader = new PivotSelectorLoader(parameters);
	}

	@Override
	public void run() {
		Progress progress = new Progress();
		ProgressReporter reporter = new ProgressReporter(progress, 250);
		
		try {
			DescriptorFile objects = DescriptorFileHeader.open(parameters.require("objects"));
			PivotSelector selector = pivotSelectorLoader.getPivotSelector();
			Metric metric = Metrics.getMetric(parameters.require("metric"));
			Iterable<Integer> pivots = selector.select(parameters.getInt("count"), objects, metric, progress);
			
			progress.setOperation("Reading pivots", parameters.getInt("count"));
			List<Descriptor> pivotDescriptors = new ArrayList<>();
			
			for (Integer id: pivots) {
				pivotDescriptors.add(objects.get(id));
				progress.incrementDone();
			}
			
			DivergenceCalculator calculator = new DivergenceCalculator();
			double divergence = calculator.calculate(pivotDescriptors);
			
			reporter.stop();
			System.out.printf("Divergence: %f\n", divergence);
		}
		catch (IOException | ParameterException ex) {
			reporter.stop();
			System.out.println(ex.getMessage());
		}
	}

	@Override
	public String getName() {
		return "Divergence";
	}

	@Override
	public String describe() {
		parameters.describe("objects", "The descriptor file to use.");
		parameters.describe("metric", "The metric to use.");
		parameters.describe("count", "The number of points to select.");
		pivotSelectorLoader.describe();
		return "Divergence test.";
	}

}
