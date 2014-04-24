package ndi.commands.descriptors;

import java.io.IOException;
import java.util.Random;

import metricspaces.Progress;
import metricspaces.descriptors.DoubleDescriptor;
import metricspaces.files.DescriptorFile;
import ndi.files.DescriptorFileLoader;

import commandline.Command;
import commandline.ParameterException;
import commandline.Parameters;
import commandline.ProgressReporter;

public class RandomDescriptorCommand implements Command {
	private Parameters parameters;
	private DescriptorFileLoader loader;
	
	
	@Override
	public void init(Parameters parameters) {
		this.parameters = parameters;
		loader = new DescriptorFileLoader(parameters);
	}

	@Override
	public void run() {
		Progress progress = new Progress();
		ProgressReporter reporter = new ProgressReporter(progress, 250);
		
		try {
			int count = parameters.getInt("count");
			int dimensions = parameters.getInt("dimensions");
			DescriptorFile objects = loader.create(parameters.require("out"), count, dimensions, "RAND" + dimensions);
			Random random = new Random();
			
			progress.setOperation("Generating", count);
			
			for (int i = 0; i < count; i++) {
				double[] data = new double[dimensions];
				
				for (int j = 0; j < dimensions; j++)
					data[j] = random.nextDouble();
				
				objects.put(new DoubleDescriptor(data));
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
		loader.describe();
		return "Randomly generates a descriptor file with the specified size and dimensions."; 
	}

}
