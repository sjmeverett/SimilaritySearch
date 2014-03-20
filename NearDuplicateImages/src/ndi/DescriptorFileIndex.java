package ndi;

import metricspaces.Progress;
import metricspaces.descriptors.Descriptor;
import metricspaces.files.DescriptorFile;

/**
 * Allows retrieval of objects from a descriptor file by the object itself rather than the position.
 * @author stewart
 *
 */
public class DescriptorFileIndex {
	private int[] index;
	private DescriptorFile<Integer, Descriptor> objects;
	
	public DescriptorFileIndex(DescriptorFile<Integer, Descriptor> objects, int size, Progress progress) {
		this.objects = objects;
		index = new int[size];
		
		progress.setOperation("Building descriptor file index", size + objects.getCapacity());
		
		for (int i = 0; i < size; i++) {
			index[i] = -1;
			progress.incrementDone();
		}
		
		for (int i = 0; i < objects.getCapacity(); i++) {
			index[objects.get(i).getObject()] = i;
			progress.incrementDone();
		}
	}
	
	/**
	 * Gets the descriptor with the specified object.
	 * @param object
	 * @return
	 */
	public Descriptor get(int object) {
		if (index[object] == -1)
			return null;
		else
			return objects.get(index[object]).getDescriptor();
	}
}
