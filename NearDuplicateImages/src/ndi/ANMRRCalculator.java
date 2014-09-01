package ndi;

import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import metricspaces.indices.ResultCollectorIndex;
import metricspaces.indices.SearchResult;
import metricspaces.indices.resultcollectors.NearestNeighboursResultCollector;
import metricspaces.util.Progress;


public class ANMRRCalculator {
	private Map<Integer, Set<Integer>> clusters;
	private ResultCollectorIndex index;
	private final int maxClusterSize2;
	
	public ANMRRCalculator(Map<Integer, Set<Integer>> clusters, ResultCollectorIndex index) {
		this.clusters = clusters;
		this.index = index;
		
		int maxClusterSize = 0;
		
		for (Set<Integer> cluster: clusters.values()) {
			int s = cluster.size() - 1;
			
			if (s > maxClusterSize)
				maxClusterSize = s;
		}
		
		maxClusterSize2 = 2 * maxClusterSize;
	}
	
	
	private int NG(int q) {
		return clusters.get(q).size() - 1;
	}
	
	private int K(int q) {
		return Math.min(4 * NG(q), maxClusterSize2);
	}
	
	private double R(int k, int q, Collection<SearchResult> results) {
		int i = 1;
		
		for (SearchResult result: results) {
			int r = result.getResult();
			
			if (r == k)
				return i;
			else if (r != q)
				i++;
		}
		
		return 1.25 * K(q);
	}
	
	private double AVR(int q) {
		NearestNeighboursResultCollector collector = new NearestNeighboursResultCollector(K(q) - 1, q);
		index.search(q, collector); 
		
		double sum = 0;
		Set<Integer> cluster = new HashSet<Integer>(clusters.get(q));
		Collection<SearchResult> results = collector.getResults();
		
		for (int k: cluster) {
			if (k == q) continue;
			sum += R(k, q, results);
		}
		
		return sum / (cluster.size() - 1);
	}
	
	private double MRR(int q) {
		return AVR(q) - 0.5 * (1 + NG(q));
	}
	
	private double NMRR(int q) {
		return MRR(q) / (1.25 * K(q) - 0.5 * (1 + NG(q)));
	}
	
	public double ANMRR(Progress progress, Integer max) {
		double sum = 0;
		double i = 0;
		
		for (int q: clusters.keySet()) {
			sum += NMRR(q);
			progress.incrementDone();
			i++;
			
			if (max != null && i >= max)
				break;
		}
		
		return sum / i;
	}
}
