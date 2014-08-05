package ndi.commands.indexes;

import java.io.IOException;

import metricspaces.Progress;
import metricspaces.indexes.resultcollectors.NearestNeighbourResultCollector;
import metricspaces.update.indices.Index;
import metricspaces.update.indices.IndexFactory;
import metricspaces.update.indices.ResultCollectorIndex;
import metricspaces.update.indices.SurrogateIndex;
import ndi.files.PairDistanceWriter;

import commandline.Command;
import commandline.ParameterException;
import commandline.Parameters;
import commandline.ProgressReporter;

public class NearestNeighbourCommand implements Command {
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
			Index temp = IndexFactory.open(parameters.require("index"), false, progress);
			
			if (!(temp instanceof ResultCollectorIndex))
				throw new ParameterException("index must implement ResultCollectorIndex");
			
			ResultCollectorIndex index = (ResultCollectorIndex)temp;
			int count = parameters.getInt("count", 1000);
			long time;
			
			PairDistanceWriter writer = new PairDistanceWriter(parameters.get("output"));
			
			if (parameters.get("surrogateRadius") != null) {
				if (!(index instanceof SurrogateIndex))
					throw new ParameterException("Index must be a SurrogateSpaceIndex if surrogateRadius is supplied.");
				
				SurrogateIndex surrogateIndex = (SurrogateIndex)index;
				double surrogateRadius = parameters.getDouble("surrogateRadius");
				progress.setOperation("Searching", count);
				
				time = System.currentTimeMillis();
				
				for (int i = 0; i < count; i++) {
					NearestNeighbourResultCollector collector = new NearestNeighbourResultCollector(i);
					
					surrogateIndex.search(i, collector, surrogateRadius);
					writer.write(i, collector.getResult(), collector.getRadius());
					progress.incrementDone();
				}
				
				time = System.currentTimeMillis() - time;
			}
			else {
				progress.setOperation("Searching", count);
				
				time = System.currentTimeMillis();
				
				for (int i = 0; i < count; i++) {
					NearestNeighbourResultCollector collector = new NearestNeighbourResultCollector(i);
					index.search(i, collector);
					writer.write(i, collector.getResult(), collector.getRadius());
					progress.incrementDone();
				}
				
				time = System.currentTimeMillis() - time;
			}
			
			writer.close();
			index.close();
			reporter.stop();
			
			System.out.printf("Average time: %.3f ms\n", (double)time / count);
		}
		catch (ParameterException | IOException ex) {
			reporter.stop();
			System.err.println(ex.getMessage());
		}
	}

	@Override
	public String getName() {
		return "NearestNeighbour";
	}

	@Override
	public String describe() {
		parameters.describe("count", "The number of queries to perform (default 1000).");
		parameters.describe("output", "The CSV file to write to.");
		parameters.describe("surrogateRadius", "The surrogate radius to use when searching surrogate indices.");
		parameters.describe("index", "The index file to use.");
		return "Finds the nearest neighbour for each query and outputs the results to a file.";
	}

}
