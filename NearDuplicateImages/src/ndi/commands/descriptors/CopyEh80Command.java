package ndi.commands.descriptors;

import java.io.IOException;
import java.util.Arrays;

import metricspaces._double.DoubleDescriptor;
import metricspaces._double.DoubleDescriptorFile;
import metricspaces.descriptors.CommonDescriptorFile;
import metricspaces.descriptors.DescriptorFile;
import metricspaces.descriptors.DescriptorFileFactory;
import metricspaces.descriptors.DescriptorFormat;
import metricspaces.util.LargeBinaryFile;
import metricspaces.util.Progress;
import commandline.Command;
import commandline.ParameterException;
import commandline.Parameters;
import commandline.ProgressReporter;

public class CopyEh80Command implements Command {
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
			DescriptorFile objects = DescriptorFileFactory.open(parameters.require("ehall"), false);
			
			if (!objects.getDescriptorName().equals("EhAll"))
				throw new ParameterException("Descriptor file is not EhAll");
			
			if (!(objects instanceof CommonDescriptorFile))
				throw new ParameterException("Descriptor file is not a common descriptor file");
			
			CommonDescriptorFile input = (CommonDescriptorFile)objects;
			DescriptorFormat<DoubleDescriptor> inputFormat = input.getCommonFormat();
			int size = input.getSize();
			
			DoubleDescriptorFile output = new DoubleDescriptorFile(new LargeBinaryFile(parameters.require("out"), true));
			DescriptorFormat<DoubleDescriptor> outputFormat = output.getFormat();
			output.writeHeader(DescriptorFile.DOUBLE_TYPE, size, 80, "Eh80");
			
			progress.setOperation("Copying", size);
			
			for (int i = 0; i < size; i++) {
				DoubleDescriptor descriptor = inputFormat.get();
				double[] data = descriptor.getData();
				double[] eh80data = Arrays.copyOf(data, 80);
				DoubleDescriptor newdescriptor = new DoubleDescriptor(eh80data);
				outputFormat.put(newdescriptor);
				progress.incrementDone();
			}
			
			input.close();
			output.close();
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
		return "Creates an Eh80 descriptor file by copying descriptors from an EhAll file."; 
	}

}
