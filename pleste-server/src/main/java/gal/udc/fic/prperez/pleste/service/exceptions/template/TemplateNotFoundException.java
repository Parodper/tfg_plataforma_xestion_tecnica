package gal.udc.fic.prperez.pleste.service.exceptions.template;

import jakarta.ws.rs.core.Response.Status;

public class TemplateNotFoundException extends TemplateExceptionTemplate {
	public TemplateNotFoundException(String id, String name) {
		super(Status.NOT_FOUND, id, name, "not found");
	}
}
