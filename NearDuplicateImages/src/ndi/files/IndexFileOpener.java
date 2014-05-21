package ndi.files;

import java.io.IOException;

import metricspaces.Progress;
import metricspaces.files.DescriptorFile;
import metricspaces.files.DescriptorFileHeader;
import metricspaces.indexes.Index;
import metricspaces.indexes.IndexFileHeader;
import metricspaces.indexes.SurrogateSpaceIndex;

import commandline.ParameterException;
import commandline.Parameters;


public class IndexFileOpener {
	private Parameters parameters;
	
	/**
	 * Constructor.
	 * @param parameters The command line parameters.
	 */
	public IndexFileOpener(Parameters parameters) {
		this.parameters = parameters;
	}
	
	
	public void describe() {
		parameters.describe("index", "The index file to use.");
		parameters.describe("originalObjects", "If the index is a surrogate, the descriptor file containing the original "
				+ "objects.");
	}
	
	
	/**
	 * Opens the specified index file.
	 * @return
	 * @throws IOException There was an error reading the index file.
	 * @throws ParameterException 
	 */
	public Index open(Progress progress) throws IOException, ParameterException {
		String path = parameters.require("index");
		String originalObjectsPath = parameters.get("originalObjects");
		
		Index index = IndexFileHeader.open(path, progress);
		
		if (originalObjectsPath != null) {
			DescriptorFile originalObjects = DescriptorFileHeader.open(originalObjectsPath);
			index = new SurrogateSpaceIndex(originalObjects, index);
		}
		
		return index;
	}
}
