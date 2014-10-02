package ndi.commands.descriptors.imagesources;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.NoSuchElementException;

import javax.imageio.ImageIO;

public class SprintfImageSource implements ImageSource {
	private File imageDirectory;
	private int to, count;
	private String format;
	private int i;
	
	public SprintfImageSource(File imageDirectory, int from, int to, String format) {
		this.imageDirectory = imageDirectory;
		this.i = from;
		this.to = to;
		this.count = to - from + 1;
		this.format = format;
	}
	
	@Override
	public BufferedImage getNext() throws IOException {
		if (!hasNext())
			throw new NoSuchElementException();
		
		File file = new File(imageDirectory, String.format(format, i));
		return ImageIO.read(file);
	}

	@Override
	public boolean hasNext() {
		return i <= to;
	}

	@Override
	public int getCount() {
		return count;
	}

	@Override
	public void close() throws IOException {
		//nothing to do
	}

}
