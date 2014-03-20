package ndi.extractors.mpeg7.colour;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;

import metricspaces.descriptors.Descriptor;
import metricspaces.metrics.Metric;
import metricspaces.metrics.SEDByComplexityMetric;
import ndi.MirFlickrUrl;

public class Test {
	public static void main(String[] args) throws MalformedURLException, IOException {
		File imageDirectory = new File("/Users/stewart/image-data");
		Metric<Descriptor> metric = new SEDByComplexityMetric();
		ColourStructureExtractor extractor = new ColourStructureExtractor();
		
		long time = System.currentTimeMillis();
		
		for (int i = 0; i < 10; i++) {
			extractor.extract(new MirFlickrUrl(0, imageDirectory).openImage());
		}
		
		time = System.currentTimeMillis() - time;
		System.out.println(time / 10);
	}
}
