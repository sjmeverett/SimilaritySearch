package ndi;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.TreeSet;

import metricspaces.Progress;
import metricspaces.descriptors.Descriptor;
import metricspaces.files.DescriptorFile;
import metricspaces.indexes.ExpandingSearch;
import metricspaces.indexes.Index;
import metricspaces.indexes.SearchResult;
import metricspaces.metrics.Metric;


/**
 * Calculates the ANMRR statistic, a retrieval rank used by MPEG-7 folks.
 * 
 * @author stewart
 * 
 */
public class ANMRRCalculator {
	private final Map<Integer, Set<Integer>> querySets;
	private final int maxK;
	private Progress progress;
	
	public ANMRRCalculator(List<Set<Integer>> clusters, Progress progress) {
		//separate the clusters into a query element (lowest key in cluster) and ground truth set
		querySets = getQuerySets(clusters);
		maxK = calculateMaxK(querySets);
		this.progress = progress;
	}
	
	
	/**
	 * Calculate ANMRR using an index.
	 * @param index
	 * @param initialRadius
	 * @param increasingFactor
	 * @return
	 */
	public double calculate(Index index, double initialRadius, double increasingFactor) {
		ExpandingSearch search = new ExpandingSearch(index, initialRadius, increasingFactor, querySets.keySet());
		double acc = 0;
		
		progress.setOperation("Calculating ANMRR", querySets.size());
		
		while (search.hasQueries()) {
			Iterator<List<SearchResult>> it = search.search();
			
			while (it.hasNext()) {
				List<SearchResult> results = it.next();
				
				if (results.size() == 0)
					continue;
				
				int query = results.get(0).getQuery();
				Set<Integer> groundTruth = querySets.get(query);
				
				int k = Math.min(4 * groundTruth.size(), maxK);
				double nmrr = calculateNMRR(query, results, groundTruth, k);
				
				//were we able to get a result?
				if (!Double.isNaN(nmrr)) {
					acc += nmrr;
					it.remove();
					progress.incrementDone();
				}
			}
		}
		
		return acc / querySets.size();
	}
	
	
	/**
	 * Calculate ANMRR using a descriptor file and metric.  This version is for non-metric distance
	 * functions, and the index version should be preferred if there is a metric distance function as
	 * it should be much faster.
	 * 
	 * @param objects
	 * @param metric
	 * @return
	 */
	public double calculate(DescriptorFile objects, Metric metric) {
		double acc = 0;
		
		progress.setOperation("Calculating ANMRR", querySets.size());
		
		for (Map.Entry<Integer, Set<Integer>> querySet: querySets.entrySet()) {
			int query = querySet.getKey();
			Set<Integer> groundTruth = querySet.getValue();
			int k = Math.min(4 * groundTruth.size(), maxK);
			
			//search for the k nearest items, exhaustively
			Collection<SearchResult> results = search(objects, metric, query, k);
			
			acc += calculateNMRR(query, results, groundTruth, k);
			progress.incrementDone();
		}
		
		return acc / querySets.size();
	}
	
	
	private Collection<SearchResult> search(DescriptorFile objects, Metric metric, int query, int k) {
		//don't care about anything past k results
		Queue<SearchResult> results = new FixedSizePriorityQueue<>(k);
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
	
	
	private double calculateNMRR(int query, Collection<SearchResult> results, Set<Integer> groundTruth, int k) {
		double acc = 0;
		int ng = groundTruth.size();
		
		Iterator<SearchResult> it = results.iterator();
		int rank;
		
		for (rank = 1; rank <= k && it.hasNext(); rank++) {
			int result = it.next().getResult();
			
			if (result == query) {
				//pretend the query isn't in the results
				rank--;
			}
			else if (groundTruth.contains(result)) {
				acc += rank;
				groundTruth.remove(result);
			}
		}
		
		if (rank > k) {
			//we got enough results
			//add 1.25k as a max value for any of the ground truth items past rank k
			acc += 1.25 * k * groundTruth.size();
		} else if (groundTruth.size() > 0) {
			//we didn't get k results back, and we didn't find all the ground truth
			//we'll need to search for this one again
			return Double.NaN;
		}
		
		double avr = acc / ng;
		return (avr - 0.5 * (1 + ng)) / (1.25 * k - 0.5 * (1 + ng));
	}
	
	
	private int calculateMaxK(Map<Integer, Set<Integer>> querySets) {
		int max = Integer.MIN_VALUE;
		
		for (Set<Integer> groundTruth: querySets.values()) {
			if (groundTruth.size() > max)
				max = groundTruth.size();
		}
		
		return 2 * max;
	}
	
	
	private Map<Integer, Set<Integer>> getQuerySets(List<Set<Integer>> clusters) {
		Map<Integer, Set<Integer>> querySets = new HashMap<>();
		
		for (Set<Integer> cluster: clusters) {
			//copy the cluster because we'll be changing it
			//using TreeSet means the query will always be the smallest ID in the cluster
			TreeSet<Integer> set = new TreeSet<>(cluster);
			int query = set.pollFirst();
			querySets.put(query, set);
		}
		
		return querySets;
	}
}
