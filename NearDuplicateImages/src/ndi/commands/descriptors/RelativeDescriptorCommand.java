package ndi.commands.descriptors;

import java.io.IOException;

import metricspaces.Progress;
import metricspaces.files.CornerSelector;
import metricspaces.files.DescriptorFile;
import metricspaces.files.DescriptorFileHeader;
import metricspaces.files.FirstNSelector;
import metricspaces.files.MaxDistanceSelector;
import metricspaces.files.ReferencePointSelector;
import metricspaces.files.RelativeDescriptorFile;
import metricspaces.files.UnitSelector;
import metricspaces.metrics.Metric;
import metricspaces.metrics.Metrics;

import commandline.Command;
import commandline.ParameterException;
import commandline.Parameters;
import commandline.ProgressReporter;


/**
 * A command for creating relative descriptors from other descriptors.
 * @author stewart
 *
 */
public class RelativeDescriptorCommand implements Command {
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
			String inputPath = parameters.require("input");
			DescriptorFile input = DescriptorFileHeader.open(inputPath);
			
			String outputPath = parameters.require("output");
			int referencePointCount = parameters.getInt("referencePointCount");
			
			DescriptorFileHeader header = new DescriptorFileHeader(outputPath, DescriptorFileHeader.RELATIVE_TYPE,
				input.getCapacity(), referencePointCount, input.getHeader().getDescriptorName() + "Relative" + referencePointCount);
			
			Metric metric = Metrics.getMetric(parameters.require("metric"));
			String selectorName = parameters.require("selector");
			ReferencePointSelector selector;
			
			if (selectorName.equals("firstn")) {
				selector = new FirstNSelector();
			}
			else if (selectorName.equals("unit")) {
				selector = new UnitSelector();
			}
			else if (selectorName.equals("max")) {
				selector = new MaxDistanceSelector(parameters.getInt("size", 10000));
			}
			else if (selectorName.equals("corner")) {
				selector = new CornerSelector();
			}
			else throw new ParameterException("selector not recognised");
			
			RelativeDescriptorFile output = new RelativeDescriptorFile(header, input, metric, selector, progress);
			
			output.copy(progress);
			
			reporter.stop();
			output.close();
		}
		catch (ParameterException ex) {
			reporter.stop();
			System.out.println(ex.getMessage());
		} 
		catch (IOException ex) {
			reporter.stop();
			System.out.println("Error reading or writing files: " + ex.getMessage());
		}
	}

	@Override
	public String getName() {
		return "RelativeDescriptor";
	}

	@Override
	public String describe() {
		parameters.describe("input", "The path to the file to copy the descriptors from. If you point this at a "
				+ "directory, a directory full of text descriptor files will be assumed.");
		parameters.describe("output", "The path to the file to copy the descriptors to.");
		parameters.describe("referencePointCount", "The number of reference points to use when creating the relative points.");
		parameters.describe("metric", "The metric to use.");
		parameters.describe("selector", "The reference point selector to use: 'firstn' to use the first points in the original "
				+ "file, 'unit' to generate unit points, 'max' to calculate the points furthest away from each other, "
				+ "or 'corner' to use points like [(0,0,0,0,...), (0,1,0,0,...), (0,1,1,0,...), ...]");
		parameters.describe("size", "For 'max' selector: the number of points to check as candidate reference points.");
		
		return "Creates a relative descriptor file from an existing descriptor file.";
	}
}
