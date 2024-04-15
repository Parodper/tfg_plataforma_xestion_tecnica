package gal.udc.fic.prperez.pleste.service.exceptions.template;

import jakarta.ws.rs.core.Response;

public class TemplateStillInUseException extends TemplateExceptionTemplate {
	public TemplateStillInUseException(Long id, String name) {
		super(Response.Status.CONFLICT, id.toString(), name, "still has component attached");
	}
}
