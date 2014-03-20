package ndi.commands.indexes;

import java.io.IOException;
import java.nio.ByteBuffer;

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
			IndexFileHeader header = new IndexFileHeader(parameters.require("file"));
			System.out.printf("Implementation: %d\n", header.getIndexImplementation());
			System.out.printf("Descriptor file: %s\n", header.getDescriptorFile());
			System.out.printf("Metric name: %s\n", header.getMetricName());
			
			if (header.getIndexImplementation() == IndexFileHeader.EXTREME_PIVOTS) {
				ByteBuffer buffer = header.getBuffer();
				System.out.printf("Number of pivots: %d\n", buffer.getInt());
				System.out.printf("Number of groups: %d\n", buffer.getInt());
				System.out.printf("Average distance: %f\n", buffer.getDouble());
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
