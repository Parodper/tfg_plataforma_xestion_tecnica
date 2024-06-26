package gal.udc.fic.prperez.pleste.service.exceptions.component;

import gal.udc.fic.prperez.pleste.service.exceptions.RESTException;
import jakarta.ws.rs.core.Response.Status;

public class ComponentFieldNotFoundException extends RESTException {
	public ComponentFieldNotFoundException(String component, String field) {
		super("Field " + field + " not found in component " + component, Status.NOT_FOUND);
	}
}
