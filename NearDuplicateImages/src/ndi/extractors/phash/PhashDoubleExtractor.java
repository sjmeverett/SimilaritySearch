package ndi.extractors.phash;

import java.awt.image.BufferedImage;
import java.io.IOException;

import metricspaces._double.DoubleDescriptor;
import metricspaces._double.DoubleDescriptorFile;
import ndi.extractors.AbstractDescriptorExtractor;

public class PhashDoubleExtractor extends AbstractDescriptorExtractor<DoubleDescriptor> {
	private final PhashExtractorBase extractor;
	
	public PhashDoubleExtractor(String path, int size) throws IOException {
		extractor = new PhashExtractorBase();
		super.init(new DoubleDescriptorFile(path, size, extractor.getDimensions(), "pHashDouble"));
	}
	
	@Override
	public DoubleDescriptor extract(BufferedImage image) {
		double[] dctvals = extractor.extract(image);
		return new DoubleDescriptor(dctvals);
	}

	@Override
	public int getDimensions() {
		return extractor.getDimensions();
	}

}
