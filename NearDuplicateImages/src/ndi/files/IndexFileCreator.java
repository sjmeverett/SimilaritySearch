package ndi.files;

import java.io.IOException;

import metricspaces.Progress;
import metricspaces.RelativePath;
import metricspaces.files.DescriptorFile;
import metricspaces.indexes.ExtremePivotsIndex;
import metricspaces.indexes.Index;
import metricspaces.indexes.IndexFileHeader;
import metricspaces.indexes.PivotedList;
import metricspaces.indexes.VantagePointTreeIndex;
import metricspaces.indexes.pivotselectors.PivotSelector;
import metricspaces.metrics.Metric;
import ndi.PivotSelectorLoader;

import commandline.ParameterException;
import commandline.Parameters;

public class IndexFileCreator {
	private Parameters parameters;
	private PivotSelectorLoader pivotSelectorLoader;
	
	/**
	 * Constructor.
	 * @param parameters The command line parameters.
	 */
	public IndexFileCreator(Parameters parameters) {
		this.parameters = parameters;
		pivotSelectorLoader = new PivotSelectorLoader(parameters);
	}
	
	
	public void describe() {
		parameters.describe("indeximplementation", "The implementation of index to use: EP for extreme pivots "
				+ ", VP for a vantage point tree or PL for a pivoted list.");
		parameters.describe("l", "For extreme pivots: the number of pivot groups to use.");
		parameters.describe("m", "For extreme pivots / pivoted list: the number of pivots to use.");
		parameters.describe("mu", "For extreme pivots: the average distance in the metric space.");
		pivotSelectorLoader.describe();
	}
	
	
	/**
	 * Creates an index file.
	 * @param path The path to the index file.
	 * @param objects The objects the index will be for.
	 * @param metric The metric to use for the index.
	 * @return
	 * @throws IOException There was an error writing the index file.
	 * @throws ParameterException The index implementation was not recognised, or mu was not specified for EP.
	 */
	public Index create(String path, DescriptorFile objects, Metric metric, int capacity, Progress progress)
			throws IOException, ParameterException {
		
		RelativePath r = new RelativePath(objects.getHeader().getPath());
		String descriptorFile = r.getRelativeTo(path);
		String indexImplementation = parameters.require("indeximplementation");
		
		if (indexImplementation.equals("VP")) {
			IndexFileHeader header = new IndexFileHeader(path, IndexFileHeader.VP_TREE, capacity,
					descriptorFile, metric.getName());
			
			return new VantagePointTreeIndex(header, objects, metric, progress);
		}
		else if (indexImplementation.equals("EP")) {
			IndexFileHeader header = new IndexFileHeader(path, IndexFileHeader.EXTREME_PIVOTS, capacity,
					descriptorFile, metric.getName());
			
			int l = parameters.getInt("l", 10);
			int m = parameters.getInt("m", 1);
			double mu = parameters.getDouble("mu");
			PivotSelector pivotSelector = pivotSelectorLoader.getPivotSelector();
			
			return new ExtremePivotsIndex(header, objects, metric, m, l, mu, pivotSelector, progress);
		}
		else if (indexImplementation.equals("PL")) {
			IndexFileHeader header = new IndexFileHeader(path, IndexFileHeader.PIVOTED_LIST, capacity,
					descriptorFile, metric.getName());
			
			int m = parameters.getInt("m", 10);
			
			return new PivotedList(header, objects, metric, m, progress);
		}
		else {
			throw new ParameterException("index implementation not recognised");
		}
	}
}
