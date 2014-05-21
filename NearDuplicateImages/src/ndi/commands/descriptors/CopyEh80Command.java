package ndi.commands.descriptors;

import java.io.IOException;
import java.util.Arrays;

import metricspaces.Progress;
import metricspaces.descriptors.Descriptor;
import metricspaces.descriptors.DoubleDescriptor;
import metricspaces.files.DescriptorFile;
import metricspaces.files.DescriptorFileHeader;
import ndi.files.DescriptorFileCreator;

import commandline.Command;
import commandline.ParameterException;
import commandline.Parameters;
import commandline.ProgressReporter;

public class CopyEh80Command implements Command {
	private Parameters parameters;
	private DescriptorFileCreator creator;
	
	@Override
	public void init(Parameters parameters) {
		this.parameters = parameters;
		creator = new DescriptorFileCreator(parameters);
	}

	@Override
	public void run() {
		Progress progress = new Progress();
		ProgressReporter reporter = new ProgressReporter(progress, 250);
		
		try {
			DescriptorFile objects = DescriptorFileHeader.open(parameters.require("ehall"));
			
			if (!objects.getHeader().getDescriptorName().equals("EhAll"))
				throw new ParameterException("Descriptor file is not EhAll");
			
			DescriptorFile eh80 = creator.create(parameters.require("out"), objects.getCapacity(), 80, "Eh80");
			progress.setOperation("Copying", objects.getCapacity());
			
			for (int i = 0; i < objects.getCapacity(); i++) {
				Descriptor descriptor = objects.get(i);
				double[] data = descriptor.getData();
				double[] eh80data = Arrays.copyOf(data, 80);
				Descriptor newdescriptor = new DoubleDescriptor(eh80data);
				eh80.put(newdescriptor);
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
		creator.describe();
		return "Creates an Eh80 descriptor file by copying descriptors from an EhAll file."; 
	}

}
