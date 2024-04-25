package gal.udc.fic.prperez.pleste.service.exceptions;

import jakarta.ws.rs.core.Response;

public class ComponentFieldNotFoundException extends RESTException {
	public ComponentFieldNotFoundException(String component, String field) {
		super("Field " + field + " not found in component " + component, Response.Status.NOT_FOUND);
	}
}
