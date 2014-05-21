package ndi.commands.descriptors;

import java.io.File;
import java.io.IOException;

import metricspaces.Progress;
import metricspaces.files.DescriptorFile;
import metricspaces.files.DescriptorFileHeader;
import metricspaces.files.TextDescriptorFile;
import ndi.files.DescriptorFileCreator;

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
			String inputPath = parameters.require("input");
			String outputPath = parameters.require("output");
			
			DescriptorFile input;
			File f = new File(inputPath);
			
			if (f.isDirectory()) {
				String filenameTemplate = parameters.get("filenameTemplate");
				
				if (filenameTemplate == null)
					throw new ParameterException("the filename template must be specified for text descriptor files");
				
				input = new TextDescriptorFile(f, filenameTemplate);
			}
			else {
				input = DescriptorFileHeader.open(inputPath);
			}

			String descriptorName = parameters.require("descriptorname");
			DescriptorFile output = creator.create(outputPath, input.getCapacity(), input.getDimensions(), descriptorName);
			
			progress.setOperation("copying", input.getCapacity());
			
			for (int i = 0; i < input.getCapacity(); i++) {
				output.put(input.get());
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

	@Override
	public String getName() {
		return "Copy";
	}

	@Override
	public String describe() {
		creator.describe();
		parameters.describe("input", "The path to the file to copy the descriptors from. If you point this at a "
				+ "directory, a directory full of text descriptor files will be assumed.");
		parameters.describe("output", "The path to the file to copy the descriptors to.");
		parameters.describe("filenameTemplate", "For text descriptor files: a sprintf style string with a placholder for "
				+ "file number, e.g. 'eh%d.txt'.");
		parameters.describe("descriptorname", "The name of the output descriptor.");
		return "Copies descriptors from one descriptor file to another.";
	}
}
