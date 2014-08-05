package ndi.extractors;

import java.awt.image.BufferedImage;
import java.io.IOException;

/**
 * Represents a method for extracting descriptors from images.
 * @author stewart
 */
public interface DescriptorExtractor {
	/**
	 * Extracts a descriptor from the specified image and saves it to a file.
	 * @param image
	 */
	public void save(BufferedImage image);
	
	/**
	 * Closes the descriptor file.
	 * @throws IOException
	 */
	public void close() throws IOException;
}
