package ndi.extractors;

import java.awt.image.BufferedImage;
import java.io.IOException;

import metricspaces.descriptors.AbstractDescriptorFile;
import metricspaces.descriptors.DescriptorFormat;

/**
 * Contains some common functionality for descriptor extractors.
 * @author stewart
 *
 * @param <DescriptorType>
 */
public abstract class AbstractDescriptorExtractor<DescriptorType> implements DescriptorExtractor, GenericDescriptorExtractor<DescriptorType> {
	protected AbstractDescriptorFile<DescriptorType> file;
	protected DescriptorFormat<DescriptorType> format;
	
	protected void init(AbstractDescriptorFile<DescriptorType> file) {
		this.file = file;
		this.format = file.getFormat();
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
