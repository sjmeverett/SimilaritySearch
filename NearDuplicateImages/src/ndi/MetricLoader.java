package ndi;

import java.util.HashMap;
import java.util.Map;

import metricspaces.descriptors.Descriptor;
import metricspaces.files.DescriptorFileHeader;
import metricspaces.metrics.CosineAngularMetric;
import metricspaces.metrics.EuclidianMetric;
import metricspaces.metrics.ManhattanMetric;
import metricspaces.metrics.Metric;
import metricspaces.metrics.SEDByComplexityMetric;
import metricspaces.metrics.quantisation.CoefficientOfRacialLikeness;
import metricspaces.metrics.quantisation.FeatureContrastModel;
import metricspaces.metrics.quantisation.HammingDistance;
import metricspaces.metrics.quantisation.Kulczvnski;
import metricspaces.metrics.quantisation.NumberOfCoOccurences;
import metricspaces.metrics.quantisation.PatternDifference;
import metricspaces.metrics.quantisation.QuantisationMetric;

import commandline.ParameterException;
import commandline.Parameters;

public class MetricLoader {
	private Parameters parameters;
	private static Map<String, Metric> metrics;
	
	public MetricLoader(Parameters parameters) {
		this.parameters = parameters;
	}
	
	private static void loadMetrics() {
		metrics = new HashMap<String, Metric>();
		addMetric(new SEDByComplexityMetric());
		addMetric(new CosineAngularMetric());
		addMetric(new ManhattanMetric());
		addMetric(new EuclidianMetric());
		addMetric(new CoefficientOfRacialLikeness(1));
		addMetric(new FeatureContrastModel(1, 1, 0));
		addMetric(new HammingDistance(1));
		addMetric(new Kulczvnski(1));
		addMetric(new NumberOfCoOccurences(1));
		addMetric(new PatternDifference(1));
	}
	
	private static void addMetric(Metric metric) {
		metrics.put(metric.getName(), metric);
	}
	
	private static Map<String, Metric> getMetrics() {
		if (metrics == null)
			loadMetrics();
		
		return metrics;
	}
	
	public Metric getMetric(DescriptorFileHeader header) throws ParameterException {
		String metricName = parameters.require("metric");
		Metric metric = getMetrics().get(metricName);
		
		if (metric == null)
			throw new ParameterException("Unrecognised metric name.");
		
		if (metric instanceof QuantisationMetric) {
			QuantisationMetric q = (QuantisationMetric)metric;
			q.setStats(header.getElementMean(), header.getElementStdDev(), header.getElementMax());
			
			if (parameters.get("p") != null) {
				q.setP(parameters.getInt("p", 1));
			}
		}
		
		return metric;
	}
	
	public Metric getMetric(String name) {
		return getMetrics().get(name);
	}
	
	public void describe() {
		parameters.describe("metric", "The name of the metric to use.");
		parameters.describe("p", "For quantisation metrics: the p value to use (the default is to use the element max for the descriptor).");
	}
}
