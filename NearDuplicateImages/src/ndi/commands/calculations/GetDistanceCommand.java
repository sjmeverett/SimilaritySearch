package ndi.commands.calculations;

import java.io.IOException;

import metricspaces.descriptors.Descriptor;
import metricspaces.descriptors.ObjectWithDescriptor;
import metricspaces.files.DescriptorFile;
import metricspaces.metrics.Metric;
import ndi.MetricLoader;
import ndi.files.DescriptorFileLoader;

import commandline.Command;
import commandline.ParameterException;
import commandline.Parameters;

public class GetDistanceCommand implements Command {
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
		try {
			String path = parameters.require("descriptors");
			DescriptorFile<Integer, Descriptor> objects = loader.load(path);
			Metric<Descriptor> metric = metrics.getMetric(objects.getHeader());
			int xid = parameters.getInt("x", -1), yid = parameters.getInt("y", -1);
			Descriptor x = null, y = null;
			
			if (xid == -1 || yid == -1)
				throw new ParameterException("values required for parameters x and y");
			
			int i = 0;
			
			for (; i < objects.getCapacity(); i++) {
				ObjectWithDescriptor<Integer, Descriptor> obj = objects.get(i);
				
				if (obj.getObject().equals(xid)) {
					x = obj.getDescriptor();
					break;
				}
			}
			
			for (; i < objects.getCapacity(); i++) {
				ObjectWithDescriptor<Integer, Descriptor> obj = objects.get(i);
				
				if (obj.getObject().equals(yid)) {
					y = obj.getDescriptor();
					break;
				}
			}
			
			if (x == null)
				throw new ParameterException("x not found");
			
			if (y == null)
				throw new ParameterException("y not found");
			
			System.out.printf("Distance: %.3f\n", metric.getDistance(x, y));
		}
		catch (ParameterException e) {
			System.out.println(e.getMessage());
		}
		catch (IOException e) {
			System.out.println("IO error: " + e.getMessage());
		}
	}

	@Override
	public String getName() {
		return "GetDistance";
	}

	@Override
	public String describe() {
		parameters.describe("descriptors", "The path to the descriptor file to run the distance caluclations over.");
		parameters.describe("x", "The ID of one of the pair.");
		parameters.describe("y", "The ID of the other of the pair.");
		metrics.describe();
		return "Calculates the distance between two objects using th specified distance function.";
	}
	
	
}