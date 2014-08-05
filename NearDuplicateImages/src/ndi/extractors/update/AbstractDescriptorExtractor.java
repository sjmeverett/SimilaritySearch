package ndi.extractors.update;

import java.awt.image.BufferedImage;
import java.io.IOException;

import metricspaces.update.common.AbstractDescriptorFile;
import metricspaces.update.common.DescriptorFormat;

/**
 * Contains some common functionality for descriptor extractors.
 * @author stewart
 *
 * @param <DescriptorType>
 */
public abstract class AbstractDescriptorExtractor<DescriptorType> implements DescriptorExtractor, GenericDescriptorExtractor<DescriptorType> {
	protected AbstractDescriptorFile<DescriptorType> file;
	protected DescriptorFormat<DescriptorType> format;
	
	
	protected AbstractDescriptorExtractor(AbstractDescriptorFile<DescriptorType> file, int size, byte descriptorType, String descriptorName) throws IOException {
		init(file, size, descriptorType, descriptorName);
	}
	
	
	protected AbstractDescriptorExtractor() {
		
	}
	
	
	protected void init(AbstractDescriptorFile<DescriptorType> file, int size, byte descriptorType, String descriptorName) throws IOException {
		this.file = file;
		file.writeHeader(descriptorType, size, getDimensions(), descriptorName);
		
		format = file.getFormat();
	}
	
	@Override
	public void save(BufferedImage image) {
		DescriptorType descriptor = extract(image);
		format.put(descriptor);
	}

	@Override
	public void close() throws IOException {
		file.close();
	}

}
