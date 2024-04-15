package gal.udc.fic.prperez.pleste.service.exceptions.template;

import jakarta.ws.rs.core.Response;

public class TemplateNotFoundException extends TemplateExceptionTemplate {
	public TemplateNotFoundException(String id, String name) {
		super(Response.Status.NOT_FOUND, id, name, "not found");
	}
}
