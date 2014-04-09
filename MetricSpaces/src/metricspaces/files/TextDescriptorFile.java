package metricspaces.files;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Scanner;

import metricspaces.descriptors.Descriptor;
import metricspaces.descriptors.DoubleDescriptor;

/**
 * An implementation of the DescriptorFile interface that gets data from 100 text files
 * with 10,000 descriptors each.  Setting the position is not supported.
 * @author stewart
 *
 */
public class TextDescriptorFile implements DescriptorFile {
	private File descriptorDirectory;
	private BufferedReader reader;
	private String filenameTemplate;
	private int dimensions, currentIndex, currentFile;
	
	public TextDescriptorFile(File descriptorDirectory, String filenameTemplate) throws IOException {
		this.descriptorDirectory = descriptorDirectory;
		this.filenameTemplate = filenameTemplate;
		currentFile = 1;
		
		File file = new File(descriptorDirectory, String.format(filenameTemplate, currentFile));
		reader = new BufferedReader(new FileReader(file));
		
		String line = reader.readLine();
		countDimensions(line);
		reader.close();
		
		reader = new BufferedReader(new FileReader(file));
	}
	
	
	@Override
	public Descriptor get() {
		try {
			String line = reader.readLine();
			
			if (dimensions == 0)
				countDimensions(line);
			
			Scanner scanner = new Scanner(line);
			double[] data = new double[dimensions];
			
			for (int i = 0; i < dimensions; i++) {
				data[i] = scanner.nextDouble();
			}
			
			scanner.close();
			currentIndex++;
			
			if ((currentIndex % 10000) == 0) {
				currentFile++;
				reader.close();
				
				if (currentFile <= 100) {
					File file = new File(descriptorDirectory, String.format(filenameTemplate, currentFile));
					reader = new BufferedReader(new FileReader(file));
				}
			}
			
			return new DoubleDescriptor(data);
		}
		catch (IOException e) {
			return null;
		}
	}

	@Override
	public Descriptor get(int index) {
		position(index);
		return get();
	}

	@Override
	public void put(Descriptor descriptor) {
		throw new UnsupportedOperationException("Writing not supported.");
	}
	
	@Override
	public void put(int index, Descriptor descriptor) {
		position(index);
		put(descriptor);
	}

	@Override
	public void position(int index) {
		throw new UnsupportedOperationException("Setting the position not supported.");
	}

	@Override
	public void close() throws IOException {
		reader.close();
	}

	@Override
	public int getCapacity() {
		return 1000000;
	}

	@Override
	public int getDimensions() {
		return dimensions;
	}
	
	@Override
	public DescriptorFileHeader getHeader() {
		throw new UnsupportedOperationException("No descriptor file header to get.");
	}
	
	private void countDimensions(String line) {
		Scanner lineScanner = new Scanner(line);		
		dimensions = 0;
		
		while (lineScanner.hasNextDouble()) {
			lineScanner.nextDouble();
			dimensions++;
		}
		
		lineScanner.close();
	}


}
