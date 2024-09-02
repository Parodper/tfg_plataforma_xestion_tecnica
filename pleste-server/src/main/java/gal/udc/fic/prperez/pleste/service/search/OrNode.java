package gal.udc.fic.prperez.pleste.service.search;

public class OrNode extends BinaryNode {
	@Override
	public String toString() {
		return "(" + getChild().toString() + " OR " + getSecondChild().toString() + ")";
	}

	@Override
	public boolean matches(Object n) {
		return getChild().matches(n) || getSecondChild().matches(n);
	}
}
