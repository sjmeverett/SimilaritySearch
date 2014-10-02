package ndi.commands.descriptors.imagesources;

import java.awt.image.BufferedImage;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileFilter;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.PathMatcher;
import java.util.NoSuchElementException;

import javax.imageio.ImageIO;

public class DirectoryImageSource implements ImageSource {
	private File[] files;
	private int i;
	private BufferedWriter listWriter;
	
	public DirectoryImageSource(File imageDirectory, String filePattern, String listPath) throws IOException {
		final PathMatcher matcher = FileSystems.getDefault().getPathMatcher("glob:" + filePattern);
		
		files = imageDirectory.listFiles(new FileFilter() {
			@Override
			public boolean accept(File pathname) {
				return matcher.matches(pathname.toPath().getFileName());
			}
		});
		
		listWriter = new BufferedWriter(new FileWriter(listPath));
	}
	
	@Override
	public BufferedImage getNext() throws IOException {
		if (!hasNext())
			throw new NoSuchElementException();
		
		BufferedImage image = ImageIO.read(files[i]);
		listWriter.write(files[i].getPath() + "\n");
		i++;
		return image;
	}

	@Override
	public boolean hasNext() {
		return i < files.length;
	}

	@Override
	public int getCount() {
		return files.length;
	}

	@Override
	public void close() throws IOException {
		listWriter.close();
	}

}
