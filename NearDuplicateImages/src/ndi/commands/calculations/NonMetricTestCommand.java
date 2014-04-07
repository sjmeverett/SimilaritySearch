package ndi.commands.calculations;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

import metricspaces.ClusterFinder;
import metricspaces.Progress;
import metricspaces.descriptors.Descriptor;
import metricspaces.descriptors.ObjectWithDescriptor;
import metricspaces.files.DescriptorFile;
import metricspaces.indexes.SearchResult;
import metricspaces.metrics.Metric;
import ndi.ImagePair;
import ndi.MetricLoader;
import ndi.files.DescriptorFileLoader;
import ndi.files.FileFormatException;
import ndi.files.ImagePairReader;
import commandline.Command;
import commandline.ParameterException;
import commandline.Parameters;
import commandline.ProgressReporter;

public class NonMetricTestCommand implements Command {
	private Parameters parameters;
	private DescriptorFileLoader loader;
	private MetricLoader metrics;
	
	@Override
	public void init(Parameters parameters) {
		this.parameters = parameters;
		loader = new DescriptorFileLoader(parameters);
		metrics = new MetricLoader(parameters);
	}

	@Override
	public void run() {
		Progress progress = new Progress();
		ProgressReporter reporter = new ProgressReporter(progress, 250);
		
		try {
			Map<Integer, Set<Integer>> images = getImages();
			DescriptorFile<Integer, Descriptor> objects = loader.load(parameters.require("objects"));
			Metric<Descriptor> metric = metrics.getMetric(objects.getHeader());
			int nearestCount = 0, totalCount = 0;
			int max = parameters.getInt("max", Integer.MAX_VALUE);
			double sqacc = 0, acc = 0;
			
			progress.setOperation("Finding nearest neighbours", Math.min(images.size(), max));
			
			for (int i = 0; i < objects.getCapacity() && totalCount < max; i++) {
				ObjectWithDescriptor<Integer, Descriptor> object = objects.get(i);
				Set<Integer> cluster = images.get(object.getObject());
				
				if (cluster != null) {
					int n = search(objects, metric, object, cluster);
					acc += n;
					sqacc += n * n;
					
					if (n == 0)
						nearestCount++;
					
					totalCount++;
					progress.incrementDone();
				}
			}
			
			reporter.stop();
			
			System.out.printf("%.0f%% have a near-duplicate as their nearest-neighbour.\n",
				(double)nearestCount / totalCount * 100);
			
			System.out.printf("Mean position: %.2f\nMean square position: %.0f\n", acc / totalCount, sqacc / totalCount);
		}
		catch (IOException e) {
			reporter.stop();
			System.out.println("IO error: " + e.getMessage());
		}
		catch (ParameterException e) {
			reporter.stop();
			System.out.println(e.getMessage());
		}
		catch (FileFormatException e) {
			reporter.stop();
			System.out.println(e.getMessage());
		}
	}
	
	private int search(DescriptorFile<Integer, Descriptor> objects, Metric<Descriptor> metric,
			ObjectWithDescriptor<Integer, Descriptor> queryObject, Set<Integer> duplicates) {
		
		Descriptor queryDescriptor = queryObject.getDescriptor();
		int queryID = queryObject.getObject();
		
		List<SearchResult<Integer>> results = new ArrayList<>();
		
		for (int i = 0; i < objects.getCapacity(); i++) {
			ObjectWithDescriptor<Integer, Descriptor> object = objects.get(i);
			
			if (!object.getObject().equals(queryID)) {
				double distance = metric.getDistance(object.getDescriptor(), queryDescriptor);
				results.add(new SearchResult<>(object.getObject(), distance, i));
			}
		}
		
		Collections.sort(results);
		
		for (int i = 0; i < results.size(); i++) {
			if (duplicates.contains(results.get(i).getResult()))
				return i;
		}
		
		throw new IllegalStateException("None of the duplicates were found.");
	}
	
	private Map<Integer, Set<Integer>> getImages() throws IOException, ParameterException, FileFormatException {
		ImagePairReader reader = new ImagePairReader(parameters.require("input"), false);
		ClusterFinder<Integer> finder = new ClusterFinder<Integer>();
		ImagePair pair;
		
		while ((pair = reader.read()) != null)
			finder.addPair(pair.getImage1(), pair.getImage2());
		
		return finder.getObjectsClusters();
	}

	@Override
	public String getName() {
		return "NonMetricTest";
	}

	@Override
	public String describe() {
		parameters.describe("input", "A CSV file containing a list of pairs of near-duplicates.");
		parameters.describe("objects", "The descriptor file to search.");
		parameters.describe("max", "The maximum number of images to assess (by default all the images in the "
				+ "file are assessed).");
		metrics.describe();
		return "Searches for each of the objects in the input file and counts how many times the nearest neighbour "
				+ "is a near duplicate.";
	}

}
