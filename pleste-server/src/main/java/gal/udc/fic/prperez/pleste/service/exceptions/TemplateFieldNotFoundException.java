package gal.udc.fic.prperez.pleste.service.exceptions;

import jakarta.ws.rs.core.Response;

public class TemplateFieldNotFoundException extends RESTException {
	public TemplateFieldNotFoundException(String template, String field) {
		super("Field " + field + " not found in template " + template, Response.Status.NOT_FOUND);
	}
}
