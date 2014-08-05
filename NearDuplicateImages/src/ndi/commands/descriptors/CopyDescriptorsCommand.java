package ndi.commands.descriptors;

import java.io.File;
import java.io.IOException;

import metricspaces.Progress;
import metricspaces.descriptors.DoubleDescriptor;
import metricspaces.update.TextDescriptorFile;
import metricspaces.update._double.DoubleDescriptorFile;
import metricspaces.update.common.CommonDescriptorFile;
import metricspaces.update.common.DescriptorFile;
import metricspaces.update.common.DescriptorFileFactory;
import metricspaces.update.common.DescriptorFormat;
import metricspaces.update.common.LargeBinaryFile;
import metricspaces.update.quantised.QuantisedDescriptorFile;
import metricspaces.update.single.SingleDescriptorFile;

import commandline.Command;
import commandline.ParameterException;
import commandline.Parameters;
import commandline.ProgressReporter;


/**
 * A command for copying descriptors from one file to another.
 * @author stewart
 *
 */
public class CopyDescriptorsCommand implements Command {
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
			String inputPath = parameters.require("input");
			String outputPath = parameters.require("output");
			
			CommonDescriptorFile input;
			File f = new File(inputPath);
			
			if (f.isDirectory()) {
				String filenameTemplate = parameters.get("filenameTemplate");
				
				if (filenameTemplate == null)
					throw new ParameterException("the filename template must be specified for text descriptor files");
				
				input = new TextDescriptorFile(f, filenameTemplate);
			}
			else {
				DescriptorFile file = DescriptorFileFactory.open(inputPath, false);
				
				if (!(file instanceof CommonDescriptorFile))
					throw new ParameterException("input must be a common descriptor file");
				
				input = (CommonDescriptorFile)file;
			}

			String descriptorName = parameters.require("descriptorname");
			CommonDescriptorFile output = create(outputPath, input.getSize(), input.getDimensions(), descriptorName);
			
			int size = input.getSize();
			progress.setOperation("copying", size);
			
			DescriptorFormat<DoubleDescriptor> inputFormat = input.getCommonFormat();
			DescriptorFormat<DoubleDescriptor> outputFormat = output.getCommonFormat();
			
			for (int i = 0; i < size; i++) {
				outputFormat.put(inputFormat.get());
				progress.incrementDone();
			}
			
			reporter.stop();
			output.close();
		}
		catch (ParameterException ex) {
			reporter.stop();
			System.out.println(ex.getMessage());
		} 
		catch (IOException ex) {
			reporter.stop();
			System.out.println("Error reading or writing files: " + ex.getMessage());
		}
	}
	
	
	private CommonDescriptorFile create(String path, int size, int dimensions, String name) throws IOException, ParameterException {
		String type = parameters.get("descriptorType");
		LargeBinaryFile file = new LargeBinaryFile(path, true);
		DescriptorFile output = null;
		Byte descriptorType = 0;
				
		if (type.equals("double")) {
			output = new DoubleDescriptorFile(file);
			descriptorType = DescriptorFile.DOUBLE_TYPE;
		}
		else if (type.equals("single")) {
			output = new SingleDescriptorFile(file);
			descriptorType = DescriptorFile.SINGLE_TYPE;
		}
		else if (type.equals("quantised")) {
			double elementMax = parameters.getDouble("elementmax", 1);
			int l1norm = parameters.getInt("l1norm", 0);
			
			output = new QuantisedDescriptorFile(file, elementMax, l1norm);
			descriptorType = DescriptorFile.QUANTISED_TYPE;
		}
		else {
			throw new ParameterException("unrecognised descriptor type");
		}
		
		output.writeHeader(descriptorType, size, dimensions, name);
		return (CommonDescriptorFile)output;
	}

	@Override
	public String getName() {
		return "Copy";
	}

	@Override
	public String describe() {
		parameters.describe("descriptorType", "The type of descriptor file to create (byte, double or single)");
		parameters.describe("elementmax", "For byte descriptors: the maximum value for any element in the descriptor. "
				+ "The elements will be scaled by this before being converted to byte. Defaults to 1.");
		parameters.describe("l1norm", "For byte descriptors: the fixed value that all descriptors sum to, or 0 if "
				+ "they are not normalised. Defaults to 0.");
		parameters.describe("input", "The path to the file to copy the descriptors from. If you point this at a "
				+ "directory, a directory full of text descriptor files will be assumed.");
		parameters.describe("output", "The path to the file to copy the descriptors to.");
		parameters.describe("filenameTemplate", "For text descriptor files: a sprintf style string with a placholder for "
				+ "file number, e.g. 'eh%d.txt'.");
		parameters.describe("descriptorname", "The name of the output descriptor.");
		return "Copies descriptors from one descriptor file to another.";
	}
}
