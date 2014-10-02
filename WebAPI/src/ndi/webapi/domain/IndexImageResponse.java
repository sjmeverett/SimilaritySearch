package ndi.webapi.domain;

import ndi.webapi.domain.vptree.Node;

public class IndexImageResponse {
	private Node node;
	
	public IndexImageResponse(Node node) {
		this.node = node;
	}
	
	public Node getNode() {
		return node;
	}
}
