package gal.udc.fic.prperez.pleste.service.exceptions.template;

import jakarta.ws.rs.core.Response;

public class TemplateStillInUseException extends TemplateExceptionTemplate {
	public TemplateStillInUseException(String id, String name) {
		super(Response.Status.CONFLICT, id, name, "still has components attached");
	}
}
