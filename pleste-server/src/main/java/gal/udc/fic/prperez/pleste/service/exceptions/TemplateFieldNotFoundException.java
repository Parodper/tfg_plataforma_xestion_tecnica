package gal.udc.fic.prperez.pleste.service.exceptions;

import jakarta.ws.rs.core.Response.Status;

public class TemplateFieldNotFoundException extends RESTException {
	public TemplateFieldNotFoundException(String template, String field) {
		super("Field " + field + " not found in template " + template, Status.NOT_FOUND);
	}
}
