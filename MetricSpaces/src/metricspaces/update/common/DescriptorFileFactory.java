package metricspaces.update.common;

import java.io.IOException;

import metricspaces.update._double.DoubleDescriptorFile;
import metricspaces.update.quantised.QuantisedDescriptorFile;
import metricspaces.update.relative.RelativeDescriptorFile;
import metricspaces.update.single.SingleDescriptorFile;

public class DescriptorFileFactory {
	public static DescriptorFile open(String path, boolean writable) throws IOException {
		LargeBinaryFile file = new LargeBinaryFile(path, writable);
		
		byte type = file.buffer.get();
		
		switch (type) {
		case DescriptorFile.QUANTISED_TYPE:
			return new QuantisedDescriptorFile(file);
		
		case DescriptorFile.DOUBLE_TYPE:
			return new DoubleDescriptorFile(file);
			
		case DescriptorFile.SINGLE_TYPE:
			return new SingleDescriptorFile(file);
			
		case DescriptorFile.RELATIVE_TYPE:
			return new RelativeDescriptorFile(file);
			
		default:
			throw new UnsupportedOperationException("Unsupported file type.");
		}
	}
}
