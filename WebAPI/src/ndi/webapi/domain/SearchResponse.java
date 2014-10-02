package ndi.webapi.domain;

import java.util.List;

public class SearchResponse {
	private List<SearchResult> results;
	private long queryTime;
	
	public SearchResponse(List<SearchResult> results, long queryTime) {
		this.results = results;
		this.queryTime = queryTime;
	}
	
	public List<SearchResult> getResults() {
		return results;
	}
	
	public long getQueryTime() {
		return queryTime;
	}
}
