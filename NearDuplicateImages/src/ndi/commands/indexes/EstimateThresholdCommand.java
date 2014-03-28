package ndi.commands.indexes;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Queue;

import metricspaces.Progress;
import metricspaces.descriptors.Descriptor;
import metricspaces.files.DescriptorFile;
import metricspaces.indexes.Index;
import metricspaces.indexes.SearchResult;
import ndi.FixedSizePriorityQueue;
import ndi.files.IndexFileLoader;

import commandline.Command;
import commandline.ParameterException;
import commandline.Parameters;
import commandline.ProgressReporter;

public class EstimateThresholdCommand implements Command {
	private Parameters parameters;
	private IndexFileLoader indexLoader;
	
	private static final int count = 1000;
	
	@Override
	public void init(Parameters parameters) {
		this.parameters = parameters;
		indexLoader = new IndexFileLoader(parameters);
	}

	@Override
	public void run() {
		Progress progress = new Progress();
		ProgressReporter reporter = new ProgressReporter(progress,  250);
		
		try {
			Index<Integer, Descriptor> index = indexLoader.load(parameters.require("index"), progress);
			DescriptorFile<Integer, Descriptor> objects = index.getObjects();
			double guessThreshold = parameters.getDouble("guess", Double.NaN);
			
			int numberOfPairs = parameters.getInt("pairs", 10) * 1000;
			int queueSize = (int)(1e9 * numberOfPairs / ((double)objects.getCapacity() * (objects.getCapacity() - 1) / 2));
			System.out.printf("Queue size: %d, Guess: %f\n", queueSize, guessThreshold);
			
			Queue<Double> queue = new FixedSizePriorityQueue<Double>(queueSize, Collections.reverseOrder());
			progress.setOperation("Calculating distances", count);
			
			for (int i = 0; i < count; i++) {
                Descriptor query = objects.get(i).getDescriptor();

                List<SearchResult<Integer>> results = index.search(query, guessThreshold);

                for (SearchResult<Integer> result: results) {
                    if (result.getResult() != i)
                        queue.offer(result.getDistance());
                }

                progress.incrementDone();
            }

            index.close();
            reporter.stop();

            if (queue.size() < queueSize)
                System.out.printf("Guess is too small: (%d).\n", queue.size());
            else
                System.out.printf("Estimated threshold: %f\n", queue.remove());
		}
		catch (ParameterException | IOException ex) {
			reporter.stop();
			System.out.println(ex.getMessage());
		}
	}

	@Override
	public String getName() {
		return "EstimateThreshold";
	}

	@Override
	public String describe() {
		parameters.describe("index", "The index file.");
		parameters.describe("guess", "A guess for the threshold - it should be larger than the the actual value, "
				+ "but not so large that the command takes a long time to run.");
		parameters.describe("pairs", "The number of thousands of pairs that the threshold should return on a"
				+ "full index search.");
		
		return "Estimates the threshold distance required to return the specified number of pairs from a "
				+ "full index search.";
	}

}
