package ndi.commands.calculations;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

import metricspaces.util.Progress;
import ndi.files.PairDistanceWriter;

import commandline.Command;
import commandline.ParameterException;
import commandline.Parameters;
import commandline.ProgressReporter;

public class SwapColumnsCommand implements Command {
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
			BufferedReader reader = new BufferedReader(new FileReader(parameters.require("input")));
			PairDistanceWriter writer = new PairDistanceWriter(parameters.require("output"));
			
			progress.setOperation("Converting", 1000000);
			reader.readLine();
			
			for (int i = 0; i < 1000000; i++) {
				String[] line = reader.readLine().split(",");
				int image1 = Integer.parseInt(line[0]);
				int image2 = Integer.parseInt(line[1]);
				double distance = Double.parseDouble(line[2]);
				
				if (image1 == i) {
					writer.writeExactly(image1, image2, distance);
				} else if (image2 == i) {
					writer.writeExactly(image2, image1, distance);
				} else {
					throw new ParameterException("Input file not in correct format");
				}
				
				progress.incrementDone();
			}
			
			reporter.stop();
			reader.close();
			writer.close();
		} catch (ParameterException | IOException ex) {
			reporter.stop();
			System.out.println(ex.getMessage());
		}
	}

	@Override
	public String getName() {
		return "SwapColumns";
	}

	@Override
	public String describe() {
		parameters.describe("input", "The input file.");
		parameters.describe("output", "The output file.");
		return "Swap the erroneous columns in files output by an older version of NearestNeighbourCommand.";
	}

}
