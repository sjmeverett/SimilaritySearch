package ndi.extractors.phash;

import java.awt.image.BufferedImage;
import java.io.IOException;

import metricspaces.hash.HashDescriptor;
import metricspaces.hash.HashDescriptorFile;
import ndi.extractors.AbstractDescriptorExtractor;

/**
 * Based off this: http://pastebin.com/Pj9d8jt5
 * @author stewart
 *
 */
public class PhashExtractor extends AbstractDescriptorExtractor<HashDescriptor> {
	private final PhashExtractorBase extractor;

	public PhashExtractor(String path, int size) throws IOException {
		extractor = new PhashExtractorBase();
		super.init(new HashDescriptorFile(path, size, extractor.getDimensions(), "pHash"));
	}
	
	public PhashExtractor() {
		extractor = new PhashExtractorBase();
	}
	

	@Override
	public HashDescriptor extract(BufferedImage image) {
		//compute the the top left 8x8 of the DCT excluding the DC component
		double[] dctvals = extractor.extract(image);
		
		//compute the average value
		double average = 0;
		
		for (int i = 0; i < dctvals.length; i++) {
			average += dctvals[i];
		}
		
		average /= dctvals.length;
		
		//make the hash
		int dimensions = extractor.getDimensions();
		byte[] data = new byte[dimensions];
		int index = 0;
		
		for (int i = 0; i < dimensions; i++) {
			byte b = 0;
			
			for (int j = 0; j < 8; j++) {
				b <<= 1;
				
				if (dctvals[index++] > average) {
					b |= 1;
				}
			}
			
			data[i] = b;
		}
		
		return new HashDescriptor(data);
	}

	@Override
	public int getDimensions() {
		return extractor.getDimensions();
	}
}
