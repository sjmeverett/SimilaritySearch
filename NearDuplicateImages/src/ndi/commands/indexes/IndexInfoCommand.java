package ndi.commands.indexes;

import java.io.IOException;
import java.util.Arrays;

import metricspaces.Progress;
import metricspaces.descriptors.DivergenceCalculator;
import metricspaces.indexes.ExtremePivotsIndex;
import metricspaces.indexes.Index;
import metricspaces.indexes.IndexFileHeader;
import commandline.Command;
import commandline.ParameterException;
import commandline.Parameters;

public class IndexInfoCommand implements Command {
	private Parameters parameters;

	@Override
	public void init(Parameters parameters) {
		this.parameters = parameters;
	}

	@Override
	public void run() {
		try {
			Index index = IndexFileHeader.open(parameters.require("file"), new Progress());
			IndexFileHeader header = index.getHeader();
			
			System.out.printf("Implementation: %d\n", header.getIndexImplementation());
			System.out.printf("Descriptor file: %s\n", header.getDescriptorFile());
			System.out.printf("Metric name: %s\n", header.getMetricName());
			
			if (header.getIndexImplementation() == IndexFileHeader.EXTREME_PIVOTS) {
				ExtremePivotsIndex ep = (ExtremePivotsIndex)index;
				System.out.printf("Number of pivots: %d\n", ep.getNumberOfPivots());
				System.out.printf("Number of groups: %d\n", ep.getNumberOfGroups());
				System.out.printf("Average distance: %f\n", ep.getMu());
				
				DivergenceCalculator divergence = new DivergenceCalculator();
				System.out.printf("Pivot divergence: %f\n", divergence.calculate(Arrays.asList(ep.getPivots())));
			}
			
			header.getFile().close();
		}
		catch (ParameterException | IOException ex) {
			System.out.println(ex.getMessage());
		}
	}

	@Override
	public String getName() {
		return "IndexInfo";
	}

	@Override
	public String describe() {
		parameters.describe("file", "The index file.");
		return "Prints information about the given index file.";
	}

}
