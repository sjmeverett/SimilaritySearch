package ndi.commands.descriptors;

import java.io.File;
import java.io.IOException;

import metricspaces.util.Progress;
import ndi.commands.descriptors.imagesources.DirectoryImageSource;
import ndi.commands.descriptors.imagesources.ImageSource;
import ndi.commands.descriptors.imagesources.MirFlickrImageSource;
import ndi.commands.descriptors.imagesources.SprintfImageSource;
import ndi.extractors.DescriptorExtractor;
import ndi.extractors.mpeg7.ColourStructureExtractor;
import ndi.extractors.mpeg7.EdgeHistogramExtractor;
import ndi.extractors.pdna.PhotoDnaExtractor;
import ndi.extractors.phash.PhashDoubleExtractor;

import commandline.Command;
import commandline.ParameterException;
import commandline.Parameters;
import commandline.ProgressReporter;


public class ExtractDescriptorCommand implements Command {
	private Parameters parameters;
	
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
			
			String imageSourceName = parameters.get("imageSource", "sprintf");
			ImageSource imageSource;
			
			if (imageSourceName.equals("sprintf")) {
				String format = parameters.require("format");
				int from = parameters.getInt("from", 0);
				int to = parameters.getInt("to");
				
				imageSource = new SprintfImageSource(imageDirectory, from, to, format);
			} else if (imageSourceName.equals("directory")) {
				String pattern = parameters.get("pattern", "*.jpg");
				String listPath = parameters.require("listPath");
				
				imageSource = new DirectoryImageSource(imageDirectory, pattern, listPath);
			} else if (imageSourceName.equals("mirflickr")) {
				imageSource = new MirFlickrImageSource(imageDirectory);
			} else {
				throw new ParameterException("Unrecognised image source name " + imageSourceName);
			}
			
			int size = imageSource.getCount();
			String descriptorName = parameters.require("descriptor");
			DescriptorExtractor extractor;
			
			if (descriptorName.equals("CS")) {
				extractor = new ColourStructureExtractor(outputPath, size);
			}
			else if (descriptorName.equals("EH")) {
				extractor = new EdgeHistogramExtractor(outputPath, size, 1100, 11);
			}
			else if (descriptorName.equals("PDNA")) {
				extractor = new PhotoDnaExtractor(outputPath, size);
			}
			else if (descriptorName.equals("pHashDouble")) {
				extractor = new PhashDoubleExtractor(outputPath, size);
			}
			else {
				throw new ParameterException("Unrecognised descriptor name.");
			}
			
			progress.setOperation("Extracting", size);
			
			while (imageSource.hasNext()) {
				extractor.save(imageSource.getNext());
				progress.incrementDone();
			}
			
			extractor.close();
			imageSource.close();
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
		parameters.describe("descriptor", "The name of the descriptor to extract [CS, EH, PDNA, pHash].");
		parameters.describe("output", "The path to the descriptor file to be created.");
		parameters.describe("imagedir", "The path to the directory containing the images.");
		parameters.describe("imageSource", "The type of image source [sprintf, mirflickr, directory] (default sprintf).");
		parameters.describe("pattern", "For imageSource=directory: the glob file pattern to accept (default *.jpg).");
		parameters.describe("listPath", "For imageSource=directory: the path to output pathnames to.");
		parameters.describe("format", "For imageSource=sprintf: the sprintf-style format for path names.");
		parameters.describe("from", "For imageSource=sprintf: the image ID to start at (default 0).");
		parameters.describe("to", "For imageSource=sprintf: the image ID to end at.");
		return "Extracts the specified descriptor from the images.";
	}
}
