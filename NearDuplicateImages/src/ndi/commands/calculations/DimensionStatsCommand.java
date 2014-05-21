package ndi.commands.calculations;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

import metricspaces.Progress;
import metricspaces.files.DescriptorFile;
import metricspaces.files.DescriptorFileHeader;

import commandline.Command;
import commandline.ParameterException;
import commandline.Parameters;
import commandline.ProgressReporter;

public class DimensionStatsCommand implements Command {
	private Parameters parameters;
	
	@Override
	public void init(Parameters parameters) {
		this.parameters = parameters;	
	}

	@Override
	public void run() {
		Progress progress = new Progress();
		ProgressReporter reporter = new ProgressReporter(progress, 500);
		
		try {
			DescriptorFile file = DescriptorFileHeader.open(parameters.require("file"));
			int count = parameters.getInt("count", file.getCapacity());
			
			progress.setOperation("Adding", count);
			double[] sums = new double[file.getDimensions()];
			
			for (int i = 0; i < count; i++) {
				double[] data = file.get(i).getData();
				
				for (int j = 0; j < data.length; j++) {
					sums[j] += data[j];
				}
				
				progress.incrementDone();
			}
			
			double[] means = new double[file.getDimensions()];
			
			for (int i = 0; i < means.length; i++) {
				means[i] = sums[i] / count;
			}
			
			progress.setOperation("Calculating variances", count);
			double[] variances = new double[file.getDimensions()];
			
			for (int i = 0; i < count; i++) {
				double[] data = file.get(i).getData();
				
				for (int j = 0; j < data.length; j++) {
					variances[j] += (data[j] - means[j]) * (data[j] - means[j]);
				}
				
				progress.incrementDone();
			}
			
			for (int i = 0; i < variances.length; i++) {
				variances[i] /= count;
			}
			
			BufferedWriter writer = new BufferedWriter(new FileWriter(parameters.require("output")));
			writer.write("mean,variance\n");
			
			for (int i = 0; i < means.length; i++) {
				writer.write(String.format("%f,%f\n", means[i], variances[i]));
			}
			
			writer.close();
			reporter.stop();
		}
		catch (IOException | ParameterException ex) {
			reporter.stop();
			System.out.println(ex.getMessage());
		}
	}

	@Override
	public String getName() {
		return "DimensionStats";
	}

	@Override
	public String describe() {
		parameters.describe("file", "The file to calculate stats for.");
		parameters.describe("count", "The number of objects to average over (default all of them).");
		parameters.describe("output", "The file to output CSV data to.");
		
		return "Calculates the mean and variance for each dimension in a descriptor file.  Outputs the results "
				+ "in a CSV file, one line per dimension, with each line of the format 'mean,variance'.";
	}

}
