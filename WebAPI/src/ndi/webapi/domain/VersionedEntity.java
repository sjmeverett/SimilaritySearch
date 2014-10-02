package ndi.webapi.domain;

import org.bson.types.ObjectId;

public interface VersionedEntity {
	public ObjectId getId();
	public int getVersion();
	public void setVersion(int version);
}
