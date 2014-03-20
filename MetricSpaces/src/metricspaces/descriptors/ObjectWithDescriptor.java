package metricspaces.descriptors;

/**
 * Represents an object paired with its descriptor.
 * @author stewart
 *
 * @param <ObjectType> The type of the object.
 * @param <DescriptorType> The type of the descriptor.
 */
public class ObjectWithDescriptor<ObjectType, DescriptorType extends Descriptor> {
	private ObjectType object;
	private DescriptorType descriptor;
	
	public ObjectWithDescriptor(ObjectType object, DescriptorType descriptor) {
		this.object = object;
		this.descriptor = descriptor;
	}
	
	public ObjectType getObject() {
		return object;
	}
	
	public DescriptorType getDescriptor() {
		return descriptor;
	}
}
