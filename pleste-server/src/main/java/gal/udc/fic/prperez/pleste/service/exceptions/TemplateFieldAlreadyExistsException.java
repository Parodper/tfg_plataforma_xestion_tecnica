package gal.udc.fic.prperez.pleste.service.exceptions;

import jakarta.ws.rs.core.Response.Status;

public class TemplateFieldAlreadyExistsException extends RESTException {
	public TemplateFieldAlreadyExistsException(String template, String name) {
		super("Field " + name + "already exists in template " + template, Status.CONFLICT);
	}
}
