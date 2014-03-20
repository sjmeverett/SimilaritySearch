package metricspaces.files;

import java.io.IOException;

import metricspaces.descriptors.Descriptor;
import metricspaces.descriptors.ObjectWithDescriptor;

/**
 * Represents an object for reading and writing large binary files containing objects and their descriptors.
 * @author stewart
 *
 * @param <ObjectType>
 * @param <DescriptorType>
 */
public interface DescriptorFile<ObjectType, DescriptorType extends Descriptor> {
	ObjectWithDescriptor<ObjectType, DescriptorType> get();
	ObjectWithDescriptor<ObjectType, DescriptorType> get(int index);
    void put(ObjectWithDescriptor<ObjectType, DescriptorType> object);
    void position(int index);
    void close() throws IOException;
    int getCapacity();
    int getDimensions();
    DescriptorFileHeader getHeader();
}
