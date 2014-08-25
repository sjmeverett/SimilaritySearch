package metricspaces.descriptors;

import java.io.IOException;

import metricspaces._double.DoubleDescriptorFile;
import metricspaces.hash.HashDescriptorFile;
import metricspaces.quantised.QuantisedDescriptorFile;
import metricspaces.relative.RelativeDescriptorFile;
import metricspaces.single.SingleDescriptorFile;
import metricspaces.util.LargeBinaryFile;

public class DescriptorFileFactory {
	public static DescriptorFile open(String path, boolean writable) throws IOException {
		LargeBinaryFile file = new LargeBinaryFile(path, writable);
		
		byte type = file.getBuffer().get();
		
		switch (type) {
		case DescriptorFile.QUANTISED_TYPE:
			return new QuantisedDescriptorFile(file);
		
		case DescriptorFile.DOUBLE_TYPE:
			return new DoubleDescriptorFile(file);
			
		case DescriptorFile.SINGLE_TYPE:
			return new SingleDescriptorFile(file);
			
		case DescriptorFile.RELATIVE_TYPE:
			return new RelativeDescriptorFile(file);
			
		case DescriptorFile.HASH_TYPE:
			return new HashDescriptorFile(file);
			
		default:
			throw new UnsupportedOperationException("Unsupported file type.");
		}
	}
}
