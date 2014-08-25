package metricspaces.hash;

public class HashDescriptor {
	private byte[] hash;
	
	public HashDescriptor(byte[] hash) {
		this.hash = hash;
	}
	
	
	public byte[] getHash() {
		return hash;
	}
}
