package ndi.commands.descriptors.imagesources;

import java.awt.image.BufferedImage;
import java.io.IOException;

/**
 * A place to get images from for descriptor extraction.
 * @author stewart
 *
 */
public interface ImageSource {
	BufferedImage getNext() throws IOException;
	
	boolean hasNext();
	
	int getCount();
	
	void close() throws IOException;
}
