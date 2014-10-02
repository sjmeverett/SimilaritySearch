package ndi.webapi.domain;

import metricspaces._double.DoubleDescriptor;

public class SearchRequest {
	private String descriptorName;
	private String metricName;
	private DoubleDescriptor query;
	private double radius;
	
	public String getDescriptorName() {
		return descriptorName;
	}
	
	public String getMetricName() {
		return metricName;
	}
	
	public DoubleDescriptor getQuery() {
		return query;
	}
	
	public double getRadius() {
		return radius;
	}
}
