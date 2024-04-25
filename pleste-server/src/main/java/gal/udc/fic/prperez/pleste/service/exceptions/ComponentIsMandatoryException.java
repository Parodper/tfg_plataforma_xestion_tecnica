package gal.udc.fic.prperez.pleste.service.exceptions;

import jakarta.ws.rs.core.Response;

public class ComponentIsMandatoryException extends RESTException {
	public ComponentIsMandatoryException(String id) {
		super("Field " + id + "can't be deleted because it is mandatory", Response.Status.BAD_REQUEST);
	}
}
