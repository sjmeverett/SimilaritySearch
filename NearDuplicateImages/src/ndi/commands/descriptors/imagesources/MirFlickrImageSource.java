package ndi.commands.descriptors.imagesources;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.NoSuchElementException;

import ndi.MirFlickrUrl;

/**
 * Reads images from the MIRFlickr directory structure. 
 * @author stewart
 *
 */
public class MirFlickrImageSource implements ImageSource {
	private int i;
	private File imageDirectory;
	
	private static final int COUNT = 1000000;
	
	public MirFlickrImageSource(File imageDirectory) {
		this.imageDirectory = imageDirectory;
	}
	
	@Override
	public BufferedImage getNext() throws IOException {
		if (!hasNext())
			throw new NoSuchElementException();
		
		return (new MirFlickrUrl(i++, imageDirectory)).openImage();
	}

	@Override
	public boolean hasNext() {
		return i < COUNT;
	}

	@Override
	public int getCount() {
		return COUNT;
	}

	@Override
	public void close() {
		//nothing to do
	}
}
