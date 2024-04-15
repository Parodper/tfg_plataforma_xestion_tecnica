package gal.udc.fic.prperez.pleste.service.exceptions.template;

import jakarta.ws.rs.core.Response;

public class TemplateAlreadyExistsException extends TemplateExceptionTemplate {
	public TemplateAlreadyExistsException(Long id, String name) {
		super(Response.Status.CONFLICT, id.toString(), name, "already exists");
	}
}
