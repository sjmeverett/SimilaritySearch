package ndi.files;

import java.io.File;
import java.io.IOException;

import metricspaces.Progress;
import metricspaces.descriptors.Descriptor;
import metricspaces.files.DescriptorFile;
import metricspaces.indexes.ExtremePivotsIndex;
import metricspaces.indexes.Index;
import metricspaces.indexes.IndexFileHeader;
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
		parameters.describe("indeximplementation", "The implementation of index to use, either EP for extreme pivots "
				+ "or VP for a vantage point tree.");
		parameters.describe("l", "For extreme pivots: the number of pivot groups to use.");
		parameters.describe("m", "For extreme pivots: the number of pivots in each group to use.");
		parameters.describe("mu", "For extreme pivots: the average distance in the metric space.");
	}
	
	
	/**
	 * Opens the specified index file.
	 * @param path The path to the index file.
	 * @return
	 * @throws IOException There was an error reading the index file.
	 */
	public Index<Integer, Descriptor> load(String path, Progress progress) throws IOException {
		path = new File(path).getAbsolutePath();
		
		IndexFileHeader header = new IndexFileHeader(path);
		Metric<Descriptor> metric = metrics.getMetric(header.getMetricName());
		String descriptorFilePath = new File(new File(path).getParentFile(), header.getDescriptorFile()).getPath();
		
		DescriptorFileLoader descriptorLoader = new DescriptorFileLoader(parameters);
		DescriptorFile<Integer, Descriptor> objects = descriptorLoader.load(descriptorFilePath);
		
		switch (header.getIndexImplementation()) {
		
		case IndexFileHeader.VP_TREE:
			return new VantagePointTreeIndex<>(header, objects, metric, progress);
		
		case IndexFileHeader.EXTREME_PIVOTS:
			@SuppressWarnings("unchecked")
			Class<Descriptor> descriptorClass = (Class<Descriptor>)objects.get(0).getDescriptor().getClass();
			return new ExtremePivotsIndex<Integer, Descriptor>(descriptorClass, header, objects, metric, progress);
		
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
	public Index<Integer, Descriptor> create(String path, DescriptorFile<Integer, Descriptor> objects,
			Metric<Descriptor> metric, Progress progress) throws IOException, ParameterException {
		
		RelativePath r = new RelativePath(objects.getHeader().getPath());
		String descriptorFile = r.getRelativeTo(path);
		String indexImplementation = parameters.get("indeximplementation");
		
		if (indexImplementation.equals("VP")) {
			IndexFileHeader header = new IndexFileHeader(path, IndexFileHeader.VP_TREE, descriptorFile, metric.getName());
			return new VantagePointTreeIndex<>(header, objects, metric, progress);
		}
		else if (indexImplementation.equals("EP")) {
			IndexFileHeader header = new IndexFileHeader(path, IndexFileHeader.EXTREME_PIVOTS, descriptorFile, metric.getName());
			int l = parameters.getInt("l", 10);
			int m = parameters.getInt("m", 1);
			double mu = parameters.getDouble("mu", Double.NaN);
			
			if (Double.isNaN(mu))
				throw new ParameterException("expected value for parameter mu");
			
			@SuppressWarnings("unchecked")
			Class<Descriptor> descriptorClass = (Class<Descriptor>)objects.get(0).getDescriptor().getClass();
			
			return new ExtremePivotsIndex<>(descriptorClass, header, objects, metric, m, l, mu, progress);
		}
		else {
			throw new ParameterException("index implementation not recognised");
		}
	}
}
