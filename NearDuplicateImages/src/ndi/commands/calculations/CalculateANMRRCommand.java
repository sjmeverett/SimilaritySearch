package ndi.commands.calculations;

import java.io.IOException;
import java.util.Map;
import java.util.Set;

import metricspaces.indices.Index;
import metricspaces.indices.IndexFactory;
import metricspaces.indices.ResultCollectorIndex;
import metricspaces.util.Progress;
import ndi.ANMRRCalculator;
import ndi.files.ClusterReader;
import ndi.files.FileFormatException;

import commandline.Command;
import commandline.ParameterException;
import commandline.Parameters;
import commandline.ProgressReporter;

public class CalculateANMRRCommand implements Command {
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
			ClusterReader reader = new ClusterReader(parameters.require("clusters"), true);
			Map<Integer, Set<Integer>> clusters = reader.read();
			Index index = IndexFactory.open(parameters.require("index"), false, progress);
			
			if (!(index instanceof ResultCollectorIndex))
				throw new ParameterException("index must be a ResultCollectorIndex");
			
			Integer max = null;
			
			if (parameters.get("max") != null)
				max = parameters.getInt("max");
			
			progress.setOperation("Calculating ANMRR", max == null ? clusters.size() : max);
			
			ANMRRCalculator calculator = new ANMRRCalculator(clusters, (ResultCollectorIndex)index);
			double ANMRR = calculator.ANMRR(progress, max);
			
			reporter.stop();
			System.out.printf("ANMRR: %.3f\n", ANMRR);
		} catch (IOException | ParameterException | FileFormatException ex) {
			reporter.stop();
			System.out.println(ex.getMessage());
		}
	}

	@Override
	public String getName() {
		return "CalculateANMRR";
	}

	@Override
	public String describe() {
		parameters.describe("index", "The index to use.");
		parameters.describe("clusters", "A CSV file containing pairs judged to be true.");
		parameters.describe("max", "If given, sets the maximum number of clusters to calculate ANMRR for.");
		return "Calcualtes the ANMRR retrieval metric for the specified index and cluster file.";
	}

}
