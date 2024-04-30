package gal.udc.fic.prperez.pleste.service.exceptions;

import gal.udc.fic.prperez.pleste.service.exceptions.template.TemplateExceptionTemplate;
import jakarta.ws.rs.core.Response;

public class TemplateFieldAlreadyExistsException extends RESTException {
	public TemplateFieldAlreadyExistsException(String template, String name) {
		super("Field " + name + "already exists in template " + template, Response.Status.CONFLICT);
	}
}
