package ndi.commands.descriptors;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import metricspaces.Progress;
import metricspaces.descriptors.Descriptor;
import metricspaces.files.DescriptorFile;
import ndi.MirFlickrUrl;
import ndi.extractors.DescriptorExtractor;
import ndi.extractors.mpeg7.colour.ColourStructureExtractor;
import ndi.extractors.mpeg7.texture.EdgeHistogramExtractor;
import ndi.extractors.pdna.PdnaExtractor;
import ndi.files.DescriptorFileLoader;

import commandline.Command;
import commandline.ParameterException;
import commandline.Parameters;
import commandline.ProgressReporter;


public class ExtractDescriptorCommand implements Command {
	private Parameters parameters;
	private DescriptorFileLoader descriptorLoader;
	private Map<String, DescriptorExtractor> descriptors;
	
	@Override
	public void init(Parameters parameters) {
		this.parameters = parameters;
		descriptorLoader = new DescriptorFileLoader(parameters);
		
		descriptors = new HashMap<String, DescriptorExtractor>();
		descriptors.put("MyEH", new EdgeHistogramExtractor(1100, 11));
		descriptors.put("CS", new ColourStructureExtractor());
		descriptors.put("PDNA", new PdnaExtractor());
	}

	@Override
	public void run() {
		Progress progress = new Progress();
		ProgressReporter reporter = new ProgressReporter(progress, 1000);
		
		try {
			String outputPath = parameters.require("output");
			File imageDirectory = new File(parameters.require("imagedir"));
			
			String descriptorName = parameters.require("descriptor");
			DescriptorExtractor extractor = descriptors.get(descriptorName);
			
			if (extractor == null)
				throw new ParameterException("Unrecognised descriptor name.");
			
			DescriptorFile descriptors = descriptorLoader.create(outputPath, 1000000,
					extractor.getDimensions(), descriptorName);
			
			progress.setOperation("Extracting", 1000000);
			
			for (int i = 0; i < 1000000; i++) {
				BufferedImage image = new MirFlickrUrl(i, imageDirectory).openImage();
				Descriptor descriptor = extractor.extract(image);
				descriptors.put(descriptor);
				
				progress.incrementDone();
			}
			
			descriptors.close();
			reporter.stop();
		}
		catch (ParameterException | IOException ex) {
			reporter.stop();
			System.out.println(ex.getMessage());
		}
	}

	@Override
	public String getName() {
		return "ExtractDescriptor";
	}

	@Override
	public String describe() {
		parameters.describe("descriptor", "The name of the descriptor to extract " + descriptors.keySet().toString() + ".");
		parameters.describe("output", "The path to the descriptor file to be created.");
		parameters.describe("imagedir", "The path to the directory containing the images.");
		
		descriptorLoader.describe();
		return "Extracts the specified descriptor from the images.";
	}
}
