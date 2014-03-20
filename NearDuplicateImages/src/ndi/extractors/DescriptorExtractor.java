package ndi.extractors;

import java.awt.image.BufferedImage;

import metricspaces.descriptors.Descriptor;

public interface DescriptorExtractor {
	Descriptor extract(BufferedImage image);
	int getDimensions();
}
