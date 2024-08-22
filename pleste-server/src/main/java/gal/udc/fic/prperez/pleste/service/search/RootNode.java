package gal.udc.fic.prperez.pleste.service.search;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.Predicate;

public class RootNode extends Node {
	@Override
	public String toString() {
		return child.toString();
	}

	@Override
	public boolean matches(Object n) {
		try {
			return getChild().matches(n);
		} catch (NullPointerException e) {
			return false;
		}
	}
}
