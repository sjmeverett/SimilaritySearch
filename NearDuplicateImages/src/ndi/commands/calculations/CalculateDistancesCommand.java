package ndi.commands.calculations;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import metricspaces.Progress;
import metricspaces.descriptors.Descriptor;
import metricspaces.files.DescriptorFile;
import metricspaces.files.DescriptorFileHeader;
import metricspaces.metrics.Metric;
import metricspaces.metrics.Metrics;
import ndi.ImagePair;
import ndi.MetricLoader;
import ndi.files.FileFormatException;
import ndi.files.ImagePairReader;
import ndi.files.PairDistanceWriter;
import commandline.Command;
import commandline.ParameterException;
import commandline.Parameters;
import commandline.ProgressReporter;

public class CalculateDistancesCommand implements Command {
	
	private Parameters parameters;
	
	@Override
	public void init(Parameters parameters) {
		this.parameters = parameters;
	}

	@Override
	public void run() {
		Progress progress = new Progress();
		ProgressReporter reporter = new ProgressReporter(progress, 250);
		
		try {
			DescriptorFile objects = DescriptorFileHeader.open(parameters.require("objects"));
			String[] metricNames = parameters.require("metric").split(",");
			Metric[] metrics = new Metric[metricNames.length];
			
			for (int i = 0; i < metricNames.length; i++) {
				metrics[i] = Metrics.getMetric(metricNames[i]);
			}
			
			List<ImagePair> pairs = getImagePairs();
			int count = Math.min(parameters.getInt("count", Integer.MAX_VALUE), pairs.size());
			progress.setOperation("Calculating distances", count);
			
			PairDistanceWriter writer = new PairDistanceWriter(parameters.require("output"), metricNames);
			
			for (ImagePair pair: pairs) {
				Descriptor x = objects.get(pair.getImage1());
				Descriptor y = objects.get(pair.getImage2());
				
				if (x == null || y == null)
					continue;
				
				double[] distances = new double[metrics.length];
				
				for (int i = 0; i < metrics.length; i++) {
					distances[i] = metrics[i].getDistance(x, y);
				}
				
				writer.write(pair.getImage1(), pair.getImage2(), distances);
				progress.incrementDone();
			}
			
			writer.close();
			reporter.stop();
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
			System.out.println("The input file was not in the correct format: " + e.getMessage());
		}
	}

	@Override
	public String getName() {
		return "CalculateDistances";
	}

	@Override
	public String describe() {
		parameters.describe("pairs", "The CSV file containing the pairs to calculate the distance between.");
		parameters.describe("objects", "The path to the descriptor file.");
		parameters.describe("count", "A maximum number of calculations to perform (defaults to all of them).");
		parameters.describe("output", "The path to the file to output the pairs with distances to.");
		parameters.describe("metric", "The metric to use, or a list of metrics separated by commas.");
		return "Calculates the distance between the pairs in an input CSV file, and writes the pairs with their "
				+ "distances back out to a CSV file.";
	}

	
	private List<ImagePair> getImagePairs() throws IOException, FileFormatException, ParameterException {
		ImagePairReader reader = new ImagePairReader(parameters.require("pairs"), true);
		List<ImagePair> pairs = new ArrayList<ImagePair>();
		ImagePair pair;
		
		while((pair = reader.read()) != null)
			pairs.add(pair);
		
		reader.close();
		return pairs;
	}
}
