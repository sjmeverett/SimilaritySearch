package metricspaces.indices;

import java.io.IOException;

import metricspaces.util.LargeBinaryFile;
import metricspaces.util.Progress;

/**
 * Opens index files.
 * @author stewart
 *
 */
public class IndexFactory {
	public static Index open(String path, boolean writable, Progress progress) throws IOException {
		LargeBinaryFile file = new LargeBinaryFile(path, writable);
		
		byte type = file.getBuffer().get();
		
		Index index = null;
		
		switch (type) {
		case Index.VP_TREE:
			index = new VPTree(file, progress);
			break;
		default:
			throw new UnsupportedOperationException("Index file not supported.");
		}
		
		if (SurrogateIndex.isSurrogateIndex(index))
			return new SurrogateIndex(index);
		else
			return index;
	}
}
