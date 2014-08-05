package ndi.extractors;

import java.awt.image.BufferedImage;


/**
 * Represents a method for extracting descriptors from images.
 * @author stewart
 */
public interface GenericDescriptorExtractor<DescriptorType> {
	/**
	 * Extracts the descriptor from the specified image, and returns it.
	 * @param image
	 * @return
	 */
	public DescriptorType extract(BufferedImage image);
	
	/**
	 * Returns the number of dimensions in the extracted descriptors.
	 * @return
	 */
	public int getDimensions();
}
