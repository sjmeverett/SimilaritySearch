package ndi.commands.descriptors;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import metricspaces.util.Progress;
import ndi.MirFlickrUrl;
import ndi.extractors.AbstractDescriptorExtractor;
import ndi.extractors.mpeg7.ColourStructureExtractor;
import ndi.extractors.mpeg7.EdgeHistogramExtractor;
import ndi.extractors.pdna.PhotoDnaExtractor;
import ndi.extractors.phash.PhashExtractor;

import commandline.Command;
import commandline.ParameterException;
import commandline.Parameters;
import commandline.ProgressReporter;

public class TimeExtractorCommand implements Command {
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
			File imageDirectory = new File(parameters.require("imagedir"));
			int count = parameters.getInt("count", 100);
			String descriptorName = parameters.require("descriptor");
			AbstractDescriptorExtractor<?> extractor;
			
			if (descriptorName.equals("CS")) {
				extractor = new ColourStructureExtractor();
			}
			else if (descriptorName.equals("EH")) {
				extractor = new EdgeHistogramExtractor(1100, 11);
			}
			else if (descriptorName.equals("PDNA")) {
				extractor = new PhotoDnaExtractor();
			}
			else if (descriptorName.equals("pHash")) {
				extractor = new PhashExtractor();
			}
			else {
				throw new ParameterException("Unrecognised descriptor name.");
			}
			
			progress.setOperation("Extracting", count);
			long time = System.currentTimeMillis();
			
			for (int i = 0; i < count; i++) {
				BufferedImage image = new MirFlickrUrl(i, imageDirectory).openImage();
				extractor.extract(image);
				progress.incrementDone();
			}
			
			time = System.currentTimeMillis() - time;
			reporter.stop();
			
			System.out.printf("Average time: %.3f ms\n", (double)time / count);
		} catch (ParameterException | IOException ex) {
			reporter.stop();
			System.out.println(ex.getMessage());
		}
	}

	@Override
	public String getName() {
		return "TimeExtractor";
	}

	@Override
	public String describe() {
		parameters.describe("imagedir", "The directory containing the images to extract descriptors from.");
		parameters.describe("count", "The number of extractions to average over (default 1000).");
		parameters.describe("descriptor", "The name of the descriptor to extract [CS, EH, PDNA, pHash]");
		return "Times how long a descriptor extractor takes on average.";
	}

}
