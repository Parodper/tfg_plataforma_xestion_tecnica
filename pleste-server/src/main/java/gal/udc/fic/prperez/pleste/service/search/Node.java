package gal.udc.fic.prperez.pleste.service.search;

public abstract class Node {
	protected Node child;

	public Node(Node child) {
		this.child = child;
	}

	public Node() {
		this.child = null;
	}

	public Node getChild() {
		return child;
	}

	public void setChild(Node child) {
		this.child = child;
	}

	public abstract String toString();

	public abstract boolean matches(Object n);
}
