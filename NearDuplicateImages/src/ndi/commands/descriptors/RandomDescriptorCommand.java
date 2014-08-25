package ndi.commands.descriptors;

import java.io.IOException;
import java.util.Random;

import metricspaces._double.DoubleDescriptor;
import metricspaces._double.DoubleDescriptorFile;
import metricspaces.descriptors.DescriptorFormat;
import metricspaces.util.Progress;

import commandline.Command;
import commandline.ParameterException;
import commandline.Parameters;
import commandline.ProgressReporter;

public class RandomDescriptorCommand implements Command {
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
			int count = parameters.getInt("count");
			int dimensions = parameters.getInt("dimensions");
			
			DoubleDescriptorFile output = new DoubleDescriptorFile(parameters.require("out"), count, dimensions, "Random" + dimensions);
			DescriptorFormat<DoubleDescriptor> outputFormat = output.getFormat();
			
			Random random = new Random();
			progress.setOperation("Generating", count);
			
			for (int i = 0; i < count; i++) {
				double[] data = new double[dimensions];
				
				for (int j = 0; j < dimensions; j++)
					data[j] = random.nextDouble();
				
				outputFormat.put(new DoubleDescriptor(data));
				progress.incrementDone();
			}
			
			reporter.stop();
		}
		catch (ParameterException | IOException ex) {
			reporter.stop();
			System.out.println(ex.getMessage());
		}
	}


	@Override
	public String getName() {
		return "RandomDescriptor";
	}

	@Override
	public String describe() {
		parameters.describe("count", "The number of descriptors to create in the file.");
		parameters.describe("dimensions", "The number of dimensions of the random descriptor.");
		parameters.describe("out", "The path of the descriptor file to create.");
		return "Randomly generates a descriptor file with the specified size and dimensions."; 
	}

}
