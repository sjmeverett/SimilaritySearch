package ndi.commands.descriptors;

import java.io.IOException;
import java.util.Arrays;

import metricspaces.Progress;
import metricspaces.descriptors.Descriptor;
import metricspaces.descriptors.DoubleDescriptor;
import metricspaces.descriptors.ObjectWithDescriptor;
import metricspaces.files.DescriptorFile;
import ndi.files.DescriptorFileLoader;

import commandline.Command;
import commandline.ParameterException;
import commandline.Parameters;
import commandline.ProgressReporter;

public class CopyEh80Command implements Command {
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
			DescriptorFile<Integer, Descriptor> objects = loader.load(parameters.require("ehall"));
			
			if (!objects.getHeader().getDescriptorName().equals("EhAll"))
				throw new ParameterException("Descriptor file is not EhAll");
			
			DescriptorFile<Integer, Descriptor> eh80 = loader.create(parameters.require("out"),
				objects.getCapacity(), 80, "Eh80");
			
			progress.setOperation("Copying", objects.getCapacity());
			
			for (int i = 0; i < objects.getCapacity(); i++) {
				ObjectWithDescriptor<Integer, Descriptor> object = objects.get(i);
				double[] data = object.getDescriptor().getData();
				double[] eh80data = Arrays.copyOf(data, 80);
				Descriptor descriptor = new DoubleDescriptor(eh80data);
				eh80.put(new ObjectWithDescriptor<>(object.getObject(), descriptor));
				progress.incrementDone();
			}
			
			eh80.close();
			objects.close();
			reporter.stop();
		}
		catch (ParameterException | IOException ex) {
			reporter.stop();
			System.out.println(ex.getMessage());
		}
	}

	@Override
	public String getName() {
		return "CopyEh80";
	}

	@Override
	public String describe() {
		parameters.describe("ehall", "The EhAll file to copy descriptors from.");
		parameters.describe("out", "The path of the Eh80 file to create.");
		loader.describe();
		return "Creates an Eh80 descriptor file by copying descriptors from an EhAll file."; 
	}

}
