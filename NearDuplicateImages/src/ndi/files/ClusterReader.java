package ndi.files;

import java.io.IOException;
import java.util.Map;
import java.util.Set;

import metricspaces.util.ClusterFinder;
import ndi.ImagePair;

public class ClusterReader {
	private ImagePairReader reader;
	
	
	public ClusterReader(String path, boolean header) throws IOException {
		reader = new ImagePairReader(path, header);
	}
	
	public Map<Integer, Set<Integer>> read() throws IOException, FileFormatException {
		ImagePair pair;
		ClusterFinder<Integer> finder = new ClusterFinder<>();
		
		while ((pair = reader.read()) != null) {
			finder.addPair(pair.getImage1(), pair.getImage2());
		}
		
		return finder.getObjectsClusters();
	}
	
	public void close() throws IOException {
		reader.close();
	}
}
