package metricspaces.descriptors;

public interface DescriptorFormat<DescriptorType>{
	/**
	 * Gets the next descriptor.
	 * @return
	 */
	public abstract DescriptorType get();
	
	/**
	 * Gets the specified descriptor.
	 * @param index
	 * @return
	 */
	public abstract DescriptorType get(int index);
	
	/**
	 * Puts the descriptor into the current position in the buffer.
	 * @param descriptor
	 */
	public abstract void put(DescriptorType descriptor);
	
	/**
	 * Puts the descriptor into the specified position in the buffer.
	 * @param index
	 * @param descriptor
	 */
    public abstract void put(int index, DescriptorType descriptor);
    
    /**
     * Sets the current position of the buffer.
     * @param index
     */
    public abstract void position(int index);
}
