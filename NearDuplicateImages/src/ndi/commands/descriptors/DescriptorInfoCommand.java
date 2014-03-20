package ndi.commands.descriptors;

import java.io.IOException;

import metricspaces.files.DescriptorFileHeader;
import commandline.Command;
import commandline.ParameterException;
import commandline.Parameters;

public class DescriptorInfoCommand implements Command {
	private Parameters parameters;
	
	@Override
	public void init(Parameters parameters) {
		this.parameters = parameters;
	}

	@Override
	public void run() {
		try {
			DescriptorFileHeader header = new DescriptorFileHeader(parameters.require("file"));
			System.out.printf("Descriptor type: %d\n", header.getDescriptorType());
			System.out.printf("Capacity: %d\n", header.getCapacity());
			System.out.printf("Dimensions: %d\n", header.getDimensions());
			System.out.printf("Descriptor name: %s\n", header.getDescriptorName());
			System.out.printf("Element mean: %.3f\n", header.getElementMean());
			System.out.printf("Element std dev: %.3f\n", header.getElementStdDev());
			System.out.printf("Element max: %.3f\n", header.getElementMax());
		}
		catch (IOException e) {
			System.out.println("IO error: " + e.getMessage());
		}
		catch (ParameterException e) {
			System.out.println(e.getMessage());
		}
	}

	@Override
	public String getName() {
		return "DescriptorInfo";
	}

	@Override
	public String describe() {
		parameters.describe("file", "The descriptor file to display the information form.");
		return "Prints out the information stored in a descriptor file header.";
	}

}
