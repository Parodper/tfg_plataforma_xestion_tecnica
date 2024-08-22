package gal.udc.fic.prperez.pleste.service.search;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.Predicate;

public class And extends BinaryNode {
	@Override
	public String toString() {
		return "(" + getChild().toString() + " AND " + getSecondChild().toString() + ")";
	}

	@Override
	public boolean matches(Object n) {
		return getChild().matches(n) && getSecondChild().matches(n);
	}
}
