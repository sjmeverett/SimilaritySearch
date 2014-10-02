package ndi.webapi.domain.vptree;

import java.util.List;

import metricspaces.util.Progress;
import ndi.webapi.domain.Config;
import ndi.webapi.domain.IndexImageRequest;
import ndi.webapi.domain.IndexImageResponse;
import ndi.webapi.domain.SearchRequest;
import ndi.webapi.domain.SearchResponse;
import ndi.webapi.domain.SearchResult;
import ndi.webapi.domain.ServiceException;

import org.jongo.Jongo;
import org.jongo.MongoCollection;

public class IndexService {
	private Jongo jongo;
	private MongoCollection indices;
	
	public IndexService() {
		jongo = Config.getJongo();
		indices = jongo.getCollection("indices");
	}
	
	public SearchResponse search(SearchRequest request) throws ServiceException {
		IndexInstance instance = getIndexInstance(request.getDescriptorName(), request.getMetricName());
		long requestTime = System.currentTimeMillis();
		List<SearchResult> results = instance.search(request.getQuery(), request.getRadius());
		
		requestTime = System.currentTimeMillis() - requestTime;
		return new SearchResponse(results, requestTime);
	}
	
	public IndexImageResponse indexImage(IndexImageRequest request) throws ServiceException {
		Index index = getIndex(request.getDescriptorName(), request.getMetricName());
		
		if (index == null) {
			index = new Index(request.getDescriptorName(), request.getMetricName());
			indices.save(index);
		}
		
		IndexInstance instance = new IndexInstance(jongo, index);
		Node node = instance.add(request.getImageId(), IndexInstance.threadSafeUpdater);
		
		return new IndexImageResponse(node);
	}
	
	public void balance(String descriptorName, String metricName, Progress progress) throws ServiceException {
		IndexInstance instance = getIndexInstance(descriptorName, metricName);
		instance.balance(progress);
	}
	
	private IndexInstance getIndexInstance(String descriptorName, String metricName) throws ServiceException {
		Index index = getIndex(descriptorName, metricName);
		
		if (index == null)
			throw new ServiceException("No index for " + descriptorName + "/" + metricName);
		
		return new IndexInstance(jongo, index);
	}
	
	private Index getIndex(String descriptorName, String metricName) {
		return indices
			.findOne("{descriptorName: #, metricName: #}", descriptorName, metricName)
			.as(Index.class);
	}
}
