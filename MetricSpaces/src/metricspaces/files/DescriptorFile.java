package metricspaces.files;

import java.io.IOException;

import metricspaces.descriptors.Descriptor;

/**
 * A file containing a list of descriptors.
 * @author stewart
 */
public interface DescriptorFile {
	Descriptor get();
	Descriptor get(int index);
	void put(Descriptor descriptor);
    void put(int index, Descriptor descriptor);
    void position(int index);
    void close() throws IOException;
    int getCapacity();
    int getDimensions();
    DescriptorFileHeader getHeader();
}
