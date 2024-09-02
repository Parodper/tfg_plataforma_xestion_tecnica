package gal.udc.fic.prperez.pleste.service.search;

public class AndNode extends BinaryNode {
	@Override
	public String toString() {
		return "(" + getChild().toString() + " AND " + getSecondChild().toString() + ")";
	}

	@Override
	public boolean matches(Object n) {
		return getChild().matches(n) && getSecondChild().matches(n);
	}
}
