package gal.udc.fic.prperez.pleste.service.exceptions.component;

import gal.udc.fic.prperez.pleste.service.exceptions.RESTException;
import jakarta.ws.rs.core.Response;

public class ComponentFieldIsMandatoryException extends RESTException {
	public ComponentFieldIsMandatoryException(String id) {
		super("Field " + id + " is mandatory", Response.Status.BAD_REQUEST);
	}
}
