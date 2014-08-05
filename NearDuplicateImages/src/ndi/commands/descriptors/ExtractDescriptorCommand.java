package ndi.commands.descriptors;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import metricspaces.Progress;
import ndi.MirFlickrUrl;
import ndi.extractors.update.ColourStructureExtractor;
import ndi.extractors.update.DescriptorExtractor;
import ndi.extractors.update.EdgeHistogramExtractor;
import ndi.extractors.update.PhotoDnaExtractor;

import commandline.Command;
import commandline.ParameterException;
import commandline.Parameters;
import commandline.ProgressReporter;


public class ExtractDescriptorCommand implements Command {
	private Parameters parameters;
	
	private static final int SIZE = 1000000;
	
	@Override
	public void init(Parameters parameters) {
		this.parameters = parameters;
	}

	@Override
	public void run() {
		Progress progress = new Progress();
		ProgressReporter reporter = new ProgressReporter(progress, 1000);
		
		try {
			String outputPath = parameters.require("output");
			File imageDirectory = new File(parameters.require("imagedir"));
			
			String descriptorName = parameters.require("descriptor");
			DescriptorExtractor extractor;
			
			if (descriptorName.equals("CS")) {
				extractor = new ColourStructureExtractor(outputPath, SIZE);
			}
			else if (descriptorName.equals("EH")) {
				extractor = new EdgeHistogramExtractor(outputPath, SIZE, 1100, 11);
			}
			else if (descriptorName.equals("PDNA")) {
				extractor = new PhotoDnaExtractor(outputPath, SIZE);
			}
			else {
				throw new ParameterException("Unrecognised descriptor name.");
			}
			
			progress.setOperation("Extracting", SIZE);
			
			for (int i = 0; i < SIZE; i++) {
				BufferedImage image = new MirFlickrUrl(i, imageDirectory).openImage();
				extractor.save(image);
				
				progress.incrementDone();
			}
			
			extractor.close();
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
		parameters.describe("descriptor", "The name of the descriptor to extract [CS, EH, PDNA].");
		parameters.describe("output", "The path to the descriptor file to be created.");
		parameters.describe("imagedir", "The path to the directory containing the images.");
		return "Extracts the specified descriptor from the images.";
	}
}
