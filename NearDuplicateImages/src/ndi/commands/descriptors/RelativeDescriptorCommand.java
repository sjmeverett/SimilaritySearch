package ndi.commands.descriptors;

import java.io.IOException;

import metricspaces.relative.RelativeDescriptorFile;
import metricspaces.util.LargeBinaryFile;
import metricspaces.util.Progress;
import commandline.Command;
import commandline.ParameterException;
import commandline.Parameters;
import commandline.ProgressReporter;


/**
 * A command for creating relative descriptors from other descriptors.
 * @author stewart
 *
 */
public class RelativeDescriptorCommand implements Command {
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
			int referencePointCount = parameters.getInt("referencePointCount");
			String inputPath = parameters.require("input");
			String metricName = parameters.require("metric");
			String selectorName = parameters.require("selector");
			String outputPath = parameters.require("output");
			
			LargeBinaryFile outputFile = new LargeBinaryFile(outputPath, true);
			RelativeDescriptorFile output = new RelativeDescriptorFile(outputFile, inputPath, metricName, selectorName, referencePointCount);
			
			output.copy(progress);
			
			reporter.stop();
			output.close();
		}
		catch (IOException | ParameterException ex) {
			reporter.stop();
			System.out.println(ex.getMessage());
		}
	}

	@Override
	public String getName() {
		return "RelativeDescriptor";
	}

	@Override
	public String describe() {
		parameters.describe("input", "The path to the file to copy the descriptors from. If you point this at a "
				+ "directory, a directory full of text descriptor files will be assumed.");
		parameters.describe("output", "The path to the file to copy the descriptors to.");
		parameters.describe("referencePointCount", "The number of reference points to use when creating the relative points.");
		parameters.describe("metric", "The metric to use.");
		parameters.describe("selector", "The reference point selector to use: 'sequential' to use the first n points in the original "
				+ "file, or 'corner' to use points like [(0,0,0,0,...), (0,1,0,0,...), (0,1,1,0,...), ...]");
		
		return "Creates a relative descriptor file from an existing descriptor file.";
	}
}
