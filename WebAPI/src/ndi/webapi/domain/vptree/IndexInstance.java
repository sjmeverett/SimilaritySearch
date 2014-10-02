package ndi.webapi.domain.vptree;

import java.util.ArrayList;
import java.util.List;

import metricspaces._double.DoubleDescriptor;
import metricspaces._double.DoubleMetricSpace;
import metricspaces.metrics.Metric;
import metricspaces.util.Progress;
import ndi.webapi.domain.Image;
import ndi.webapi.domain.OptimisticUpdateCallback;
import ndi.webapi.domain.SearchResult;
import ndi.webapi.domain.ServiceException;
import ndi.webapi.domain.vptree.NodeList.ObjectPointer;
import ndi.webapi.domain.vptree.NodeList.QuickSelectResult;

import org.bson.types.ObjectId;
import org.jongo.Jongo;
import org.jongo.MongoCollection;
import org.jongo.MongoCursor;

public class IndexInstance {
	private Jongo jongo;
	private MongoCollection nodes, images, indices;
	private Index index;
	private Node root;
	private Metric<DoubleDescriptor> metric;
	private IndexRepository repository;
	
	public IndexInstance(Jongo jongo, Index index) {
		this.jongo = jongo;
		nodes = jongo.getCollection("nodes");
		images = jongo.getCollection("images");
		indices = jongo.getCollection("indices");
		repository = new IndexRepository(jongo);
		this.index = index;
		
		metric = DoubleMetricSpace.getMetric(index.getMetricName());
		
		if (index.getRootNode() != null)
			root = repository.getNode(index.getRootNode());
	}
	
	public Node add(ObjectId imageId, AddNodeUpdater updater) throws ServiceException {
		Image image = images.findOne(imageId).as(Image.class);
		
		if (image == null)
			throw new ServiceException("Could not find image: " + imageId.toString());
		
		Node node = new Node(imageId, index.getId());
		repository.insertNode(node);
				
		return add(node, updater);
	}
	
	private Node add(Node node, AddNodeUpdater updater) throws ServiceException {
		if (root == null) {
			OptimisticResult<Index> result = repository.tryUpdateIndex(index, new OptimisticUpdateCallback<Index>() {
				@Override
				public boolean callback(Index current, Object[] params) {
					return current.getRootNode() == null;
				}
			}, "{rootNode: #}", node.getId());
			
			index = result.getResult();
			
			if (result.success()) {
				root = node;
				return node;
			} else {
				root = repository.getNode(index.getRootNode());
			}
		}
		
		return add(root, node, getDescriptor(node.getImageId()), 1, updater);
	}
	
	
	private Node add(Node parent, Node node, DoubleDescriptor nodeDescriptor, int depth, AddNodeUpdater updater) {
		while (true) {
			DoubleDescriptor parentDescriptor = getDescriptor(parent.getImageId());
			double distance = metric.getDistance(parentDescriptor, nodeDescriptor);
			
			if (parent.getLeft() == null) {
				Node result = updater.update(nodes, parent, node, distance, 0);
				
				if (result != null) {
					return result;
				}
			} else if (distance <= parent.getRadius()) {
				parent = repository.getNode(parent.getLeft());
				depth++;
			} else if (parent.getRight() == null) {
				Node result = updater.update(nodes, parent, node, distance, 1);
				
				if (result != null) {
					return result;
				}
			} else {
				parent = repository.getNode(parent.getRight());
				depth++;
			}
		}
	}
	
	public Index balance(Progress progress) {
		MongoCursor<Node> currentNodes = nodes.find("{indexId: #}", index.getId()).as(Node.class);
		NodeList list = new NodeList(jongo, currentNodes, metric, index.getDescriptorName());
		
		Index newIndex = new Index(index.getDescriptorName(), index.getMetricName());
		indices.save(newIndex);
		
		int count = (int)nodes.count();
		progress.setOperation("Balancing", count);
		newIndex.setRootNode(createNode(newIndex.getId(), list, 0, count - 1, null, progress));
		indices.save(newIndex);
		
		nodes.remove("{indexId: #}", index.getId());
		indices.remove(index.getId());
		index = newIndex;
		return index;
	}
	
	//TODO: convert recursion to iteration
	private ObjectId createNode(ObjectId newIndexId, NodeList nodes, int start, int end, ObjectPointer parent, Progress progress) {
        ObjectPointer vantagePointPtr = nodes.get(start);
        ObjectId vantagePointId = vantagePointPtr.getImageId();
        DoubleDescriptor vantagePoint = getDescriptor(vantagePointId);
        
        Node node = new Node(vantagePointId, newIndexId);
        int size = end - start + 1;

        if (size > 1) {
            //take the vantage point out the list
            start++;

            //set the radius to be the median distance from the vantage point to all the other points
            int medianIndex = (end - start) / 2 + start;
            QuickSelectResult median = nodes.quickSelect(start, end, medianIndex, vantagePointId, vantagePoint);
            node.setRadius(median.getDistance());

            //everything less than or equal to the median distance (i.e., inside the circle)
            //goes in the left subtree
            node.setLeft(createNode(newIndexId, nodes, start, medianIndex, vantagePointPtr, progress));

            //everything greater than the median distance (i.e., outside the circle) goes in
            //the right subtree (as long as there are points left)
            if (end > medianIndex)
                node.setRight(createNode(newIndexId, nodes, medianIndex + 1, end, vantagePointPtr, progress));
        }


        if (parent != null)
            node.setDistanceToParent(parent.getDistance(vantagePointId, vantagePoint));
        
        this.nodes.save(node);

        progress.incrementDone();
        return node.getId();
    }
	
	public List<SearchResult> search(DoubleDescriptor query, double searchRadius) {
		List<SearchResult> results = new ArrayList<>();
		
		search(root, query, searchRadius, results, Double.NaN);
		return results;
	}
	
	//TODO: convert recursion to iteration
	private void search(Node node, DoubleDescriptor query, double searchRadius, List<SearchResult> results, double parentToQueryDistance) {
		if (Double.isNaN(parentToQueryDistance) || Double.isNaN(node.getDistanceToParent())
			|| Math.abs(node.getDistanceToParent() - parentToQueryDistance) <= node.getRadius() + searchRadius) {
			
			DoubleDescriptor nodeDescriptor = getDescriptor(node.getImageId());
			double distance = metric.getDistance(nodeDescriptor, query);
			
			if (distance <= searchRadius) {
				results.add(new SearchResult(getImage(node), distance));
			}
			
			if (node.getLeft() != null && distance <= node.getRadius() + searchRadius) {
				search(repository.getNode(node.getLeft()), query, searchRadius, results, distance);
			}
			
			if (node.getRight() != null && distance >= node.getRadius() - searchRadius) {
				search(repository.getNode(node.getRight()), query, searchRadius, results, distance);
			}
		} else if (node.getRight() != null) {
			search(repository.getNode(node.getRight()), query, searchRadius, results, Double.NaN);
		}
	}
	
	private DoubleDescriptor getDescriptor(ObjectId id) {
		Image image = images
			.findOne(id)
			.projection("{'descriptors." + index.getDescriptorName() + "': 1}")
			.as(Image.class);
		
		return image.getDescriptor(index.getDescriptorName());
	}
	
	private Image getImage(Node node) {
		Image image = images
			.findOne(node.getImageId())
			.projection("{imageUrl: 1}")
			.as(Image.class);
		
		return image;
	}
	
	
	public interface AddNodeUpdater {
		Node update(MongoCollection nodes, Node parent, Node node, double distance, int leftOrRight);
	}
	
	public static AddNodeUpdater fastAddNodeUpdater = new AddNodeUpdater() {
		@Override
		public Node update(MongoCollection nodes, Node parent, Node node, double distance, int leftOrRight) {
			if (leftOrRight == 0) {
				parent.setLeft(node.getId());
				parent.setRadius(distance);
			} else {
				parent.setRight(node.getId());
			}
			nodes.save(parent);
			node.setDistanceToParent(distance);
			nodes.save(node);
			return node;
		}
	};
	
	public static AddNodeUpdater threadSafeUpdater = new AddNodeUpdater() {
		@Override
		public Node update(MongoCollection nodes, Node parent, Node node, double distance, int leftOrRight) {
			Node result;
			
			if (leftOrRight == 0) {
				result = nodes
					.findAndModify("{_id: #, version: #}", parent.getId(), parent.getVersion())
					.with("{$inc: {version: 1}, $set: {left: #, radius: #}}", node.getId(), distance)
					.as(Node.class);
			} else {
				result = nodes
					.findAndModify("{_id: #, version: #}", parent.getId(), parent.getVersion())
					.with("{$inc: {version: 1}, $set: {right: #}}", node.getId())
					.as(Node.class);
			}
			
			if (result != null) {
				while (true) {
					Node updated = nodes
						.findAndModify("{_id: #, version: #}", node.getId(), node.getVersion())
						.with("{$inc: {version: 1}, $set: {distanceToParent: #}}", distance)
						.as(Node.class);
					
					if (updated == null) {
						node = nodes.findOne(node.getId()).as(Node.class);
					} else {
						return updated;
					}
				}
			} else {
				return null;
			}
		}
	};
}