package ndi.files;

import java.io.File;
import java.io.IOException;

import metricspaces.Progress;
import metricspaces.files.DescriptorFile;
import metricspaces.indexes.ExtremePivotsIndex;
import metricspaces.indexes.Index;
import metricspaces.indexes.IndexFileHeader;
import metricspaces.indexes.PivotedList;
import metricspaces.indexes.VantagePointTreeIndex;
import metricspaces.metrics.Metric;
import ndi.MetricLoader;
import ndi.RelativePath;

import commandline.ParameterException;
import commandline.Parameters;

/**
 * Provides methods for loading and creating index files.
 * @author stewart
 *
 */
public class IndexFileLoader {
	private Parameters parameters;
	private MetricLoader metrics;
	
	/**
	 * Constructor.
	 * @param parameters The command line parameters.
	 */
	public IndexFileLoader(Parameters parameters) {
		this.parameters = parameters;
		metrics = new MetricLoader(parameters);
	}
	
	
	public void describe() {
		parameters.describe("indeximplementation", "The implementation of index to use: EP for extreme pivots "
				+ ", VP for a vantage point tree or PL for a pivoted list.");
		parameters.describe("l", "For extreme pivots: the number of pivot groups to use.");
		parameters.describe("m", "For extreme pivots / pivoted list: the number of pivots to use.");
		parameters.describe("mu", "For extreme pivots: the average distance in the metric space.");
	}
	
	
	/**
	 * Opens the specified index file.
	 * @param path The path to the index file.
	 * @return
	 * @throws IOException There was an error reading the index file.
	 */
	public Index load(String path, Progress progress) throws IOException {
		path = new File(path).getAbsolutePath();
		
		IndexFileHeader header = new IndexFileHeader(path);
		Metric metric = metrics.getMetric(header.getMetricName());
		String descriptorFilePath = new File(new File(path).getParentFile(), header.getDescriptorFile()).getPath();
		
		DescriptorFileLoader descriptorLoader = new DescriptorFileLoader(parameters);
		DescriptorFile objects = descriptorLoader.load(descriptorFilePath);
		
		switch (header.getIndexImplementation()) {
		
		case IndexFileHeader.VP_TREE:
			return new VantagePointTreeIndex(header, objects, metric, progress);
		
		case IndexFileHeader.EXTREME_PIVOTS:
			return new ExtremePivotsIndex(header, objects, metric, progress);
		
		case IndexFileHeader.PIVOTED_LIST:
			return new PivotedList(header, objects, metric, progress);
			
		default:
			throw new UnsupportedOperationException("index type not supported");
		}
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
		String indexImplementation = parameters.get("indeximplementation");
		
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
			
			return new ExtremePivotsIndex(header, objects, metric, m, l, mu, progress);
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
