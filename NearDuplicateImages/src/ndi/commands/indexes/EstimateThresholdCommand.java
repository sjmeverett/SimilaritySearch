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

public class Get5kThresholdCommand implements Command {
	private Parameters parameters;
	private IndexFileLoader indexLoader;
	
	private static final int count = 10000;
    private static final int queueSize = 50;
	
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

            if (queue.size() < 50)
                System.out.printf("Guess is too small: (%d).\n", queue.size());
            else
                System.out.printf("Estimated 5k threshold: %f\n", queue.remove());
		}
		catch (ParameterException | IOException ex) {
			reporter.stop();
			System.out.println(ex.getMessage());
		}
	}

	@Override
	public String getName() {
		return "Get5kThreshold";
	}

	@Override
	public String describe() {
		parameters.describe("index", "The index file.");
		parameters.describe("guess", "A guess for the 5k threshold - it should be larger than the the actual value, "
				+ "but not so large that the command takes a long time to run.");
		
		return "Performs a search on the dataset using the first 10,000 images as queries.  The closest 50 distances " +
		        "are kept - the 50th distance should give an indication of the search threshold required to return "
		        + "5,000 images.";
	}

}
