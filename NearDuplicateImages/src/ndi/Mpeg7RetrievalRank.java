package ndi;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Queue;
import java.util.Set;
import java.util.TreeSet;

import metricspaces.Progress;
import metricspaces.descriptors.Descriptor;
import metricspaces.files.DescriptorFile;
import metricspaces.indexes.SearchResult;
import metricspaces.metrics.Metric;


/**
 * Utility class for calculating the MPEG-7 ANMRR (Average Normalised Modified Retrieval Rank)
 * statistic for a given search strategy and list of known near duplicate pair clusters.
 * @author stewart
 *
 */
public class Mpeg7RetrievalRank {
	private int maxK;
	private List<Set<Integer>> clusters;
	private Progress progress;
	
	public Mpeg7RetrievalRank(List<Set<Integer>> clusters, Progress progress) {
		this.clusters = clusters;
		this.progress = progress;
		
		maxK = Integer.MIN_VALUE;
		
		for (Set<Integer> cluster: clusters) {
			int ngq = cluster.size() - 1;
			
			if (ngq > maxK)
				maxK = ngq;
		}
		
		maxK *= 2;
	}
	
	
	public double getANMRR(DescriptorFile objects, Metric metric, int max) {
		double acc = 0;
		progress.setOperation("Calculating ANMRR", Math.min(clusters.size(), max));
		
		for (Set<Integer> cluster: clusters) {
			//getNMRR modifies the set, so copy it
			//also want to ensure the cluster is sorted so that it always uses the
			//least ID for the query
			Set<Integer> copy = new TreeSet<Integer>(cluster);
			acc += getNMRR(objects, metric, copy);
			progress.incrementDone();
			
			if (progress.getDone() >= max)
				break;
		}
		
		return acc / clusters.size();
	}
	
	
	private double getNMRR(DescriptorFile objects, Metric metric, Set<Integer> cluster) {
		Iterator<Integer> it = cluster.iterator();
		int query = it.next();
		it.remove();
		
		int ng = cluster.size();
		int k = Math.min(4 * ng, maxK);
		
		//calculate the average retrieval rank for this query
		Iterator<SearchResult> results = search(objects, metric, query, k).iterator();
		double acc = 0;
		
		for (int i = 0; i < k; i++) {
			int result = results.next().getResult();
			
			if (cluster.contains(result)) {
				acc += i + 1;
				cluster.remove(result);
			}
		}
		
		//add the max value for anything past the max rank
		for (int i = 0; i < cluster.size(); i++)
			acc += 1.25 * k;
		
		double avr = acc / ng;
		
		//normalised modified retrieval rank
		return (avr - 0.5 * (1 + ng)) / (1.25 * k - 0.5 * (1 + ng));
	}
	
	
	private Collection<SearchResult> search(DescriptorFile objects, Metric metric,
			int query, int k) {
		
		//don't care about anything past k results
		Queue<SearchResult> results = new FixedSizePriorityQueue<>(k, null);
		Descriptor queryDescriptor = objects.get(query);
		
		//calculate the distance between the query and all the rest of the items
		for (int i = 0; i < objects.getCapacity(); i++) {
			//every query should have itself as a result, so skip it
			if (i == query)
				continue;
			
			Descriptor descriptor = objects.get(i);
			double distance = metric.getDistance(descriptor, queryDescriptor);
			results.add(new SearchResult(i, distance));
		}
		
		return results;
	}
}
