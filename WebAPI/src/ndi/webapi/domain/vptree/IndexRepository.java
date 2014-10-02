package ndi.webapi.domain.vptree;

import ndi.webapi.domain.OptimisticUpdateCallback;
import ndi.webapi.domain.VersionedEntity;

import org.bson.types.ObjectId;
import org.jongo.Jongo;
import org.jongo.MongoCollection;

import com.mongodb.WriteResult;

public class IndexRepository {
	private MongoCollection nodes;
	private MongoCollection indices;
	
	public IndexRepository(Jongo jongo) {
		nodes = jongo.getCollection("nodes");
		indices = jongo.getCollection("indices");
	}
	
	public void insertNode(Node node) {
		nodes.insert(node);
	}
	
	public Node getNode(ObjectId id) {
		return nodes
			.findOne(id)
			.as(Node.class);
	}
	
	public OptimisticResult<Node> tryUpdateNode(Node node, OptimisticUpdateCallback<Node> callback, String set, Object... params) {
		return tryUpdate(nodes, node, Node.class, callback, set, params);
	}
	
	public OptimisticResult<Index> tryUpdateIndex(Index index, OptimisticUpdateCallback<Index> callback, String set, Object... params) {
		return tryUpdate(indices, index, Index.class, callback, set, params);
	}
	
	private <T extends VersionedEntity> OptimisticResult<T> tryUpdate(MongoCollection collection, T entity, Class<T> entityClass, String set, Object... params) {
		T result = collection
			.findAndModify("{_id: #, version: #}", entity.getId(), entity.getVersion())
			.with("{$inc: {version: 1}, $set: " + set + "}}", params)
			.returnNew()
			.as(entityClass);
		
		if (result == null) {
			result = collection.findOne(entity.getId()).as(entityClass);
		}
		
		return new OptimisticResult<>(result != null, result);
	}
	
	private <T extends VersionedEntity> OptimisticResult<T> tryUpdate(MongoCollection collection, T entity, Class<T> entityClass, OptimisticUpdateCallback<T> callback, String set, Object... params) {
		do {
			OptimisticResult<T> result = tryUpdate(collection, entity, entityClass, set, params);
			entity = result.getResult();
			
			if (result.success()) {
				return result;
			}
		} while(callback.callback(entity, params));
		
		return new OptimisticResult<>(false, entity);
	}
}
