package gal.udc.fic.prperez.pleste.service.exceptions.template;

import jakarta.ws.rs.core.Response.Status;

public class TemplateAlreadyExistsException extends TemplateExceptionTemplate {
	public TemplateAlreadyExistsException(Long id, String name) {
		super(Status.CONFLICT, id.toString(), name, "already exists");
	}
}
