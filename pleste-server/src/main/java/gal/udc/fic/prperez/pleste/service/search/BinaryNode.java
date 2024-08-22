package gal.udc.fic.prperez.pleste.service.search;

public abstract class BinaryNode extends Node {
	protected Node secondChild;

	public BinaryNode(Node child, Node secondChild) {
		super(child);
		this.secondChild = secondChild;
	}

	public BinaryNode() {
		super();
		this.secondChild = null;
	}

	public Node getSecondChild() {
		return secondChild;
	}

	public void setSecondChild(Node secondChild) {
		this.secondChild = secondChild;
	}
}
