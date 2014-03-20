package ndi.commands.descriptors;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Set;

import metricspaces.Progress;
import metricspaces.descriptors.Descriptor;
import metricspaces.descriptors.ObjectWithDescriptor;
import metricspaces.files.DescriptorFile;
import metricspaces.files.TextDescriptorFile;
import ndi.files.DescriptorFileLoader;
import ndi.files.IdFileReader;
import commandline.Command;
import commandline.ParameterException;
import commandline.Parameters;
import commandline.ProgressReporter;


/**
 * A command for copying descriptors from one file to another, optionally including only the objects specified
 * in a file.
 * @author stewart
 *
 */
public class CopyDescriptorsCommand implements Command {
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
			String inputPath = parameters.require("input");
			String outputPath = parameters.require("output");
			
			DescriptorFile<Integer, Descriptor> input;
			File f = new File(inputPath);
			
			if (f.isDirectory()) {
				String filenameTemplate = parameters.get("filenameTemplate");
				
				if (filenameTemplate == null)
					throw new ParameterException("the filename template must be specified for text descriptor files");
				
				input = new TextDescriptorFile(f, filenameTemplate);
			}
			else {
				input = loader.load(inputPath);
			}

			Set<Integer> ids = loadIDs();
			
			int count = ids == null ? input.getCapacity() : ids.size();
			String descriptorName = parameters.require("descriptorname");
			DescriptorFile<Integer, Descriptor> output = loader.create(outputPath, count, input.getDimensions(), descriptorName);
			
			progress.setOperation("copying", input.getCapacity());
			
			for (int i = 0; i < input.getCapacity(); i++) {
				ObjectWithDescriptor<Integer, Descriptor> object = input.get();
				
				if (ids == null || ids.contains(object.getObject())) {
					output.put(object);
				}
				
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
		loader.describe();
		parameters.describe("input", "The path to the file to copy the descriptors from. If you point this at a "
				+ "directory, a directory full of text descriptor files will be assumed.");
		parameters.describe("output", "The path to the file to copy the descriptors to.");
		parameters.describe("ids", "A file containing a list of object IDs to copy (optional: if ommitted, all "
				+ "objects are copied).");
		parameters.describe("filenameTemplate", "For text descriptor files: a sprintf style string with a placholder for "
				+ "file number, e.g. 'eh%d.txt'.");
		parameters.describe("descriptorname", "The name of the output descriptor.");
		return "Copies descriptors from one descriptor file to another.";
	}

	private Set<Integer> loadIDs() throws FileNotFoundException {
		String idsPath = parameters.get("ids");
		
		if (idsPath != null) {
			IdFileReader reader = new IdFileReader(idsPath);
			return reader.read();
		}
		else {
			return null;
		}
	}
}
