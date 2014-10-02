package ndi.webapi.domain.vptree;

import java.util.HashSet;
import java.util.Set;

import ndi.webapi.domain.VersionedEntity;

import org.bson.types.ObjectId;


public class Index implements VersionedEntity {
	private ObjectId _id;
	private ObjectId rootNode;
	private String metricName;
	private String descriptorName;
	private int minDepth, maxDepth;
	private Set<ObjectId> shallowestNodes;
	private int version;
	
	public Index() {
		shallowestNodes = new HashSet<>();
	}
	
	public Index(String descriptorName, String metricName) {
		this();
		this.descriptorName = descriptorName;
		this.metricName = metricName;
	}
	
	public ObjectId getId() {
		return _id;
	}
	
	public ObjectId getRootNode() {
		return rootNode;
	}
	
	public void setRootNode(ObjectId rootNode) {
		this.rootNode = rootNode;
	}
	
	public String getMetricName() {
		return metricName;
	}
	
	public String getDescriptorName() {
		return descriptorName;
	}
	
	public int getMinDepth() {
		return minDepth;
	}
	
	public int getMaxDepth() {
		return maxDepth;
	}
	
	public void setMaxDepth(int maxDepth) {
		this.maxDepth = maxDepth;
	}
	
	public boolean isShallowNode(ObjectId id) {
		return shallowestNodes.contains(id);
	}
	
	public boolean removeShallowNode(ObjectId id) {
		shallowestNodes.remove(id);
		return shallowestNodes.isEmpty();
	}
	
	public void addShallowNode(ObjectId id) {
		shallowestNodes.add(id);
	}
	
	public void incrementMinDepth() {
		minDepth++;
	}
	
	public int getVersion() {
		return version;
	}
	
	public void setVersion(int version) {
		this.version = version;
	}
}
