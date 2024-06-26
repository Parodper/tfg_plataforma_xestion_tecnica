package gal.udc.fic.prperez.pleste.service.exceptions.template;

import jakarta.ws.rs.core.Response.Status;

public class TemplateStillInUseException extends TemplateExceptionTemplate {
	public TemplateStillInUseException(String id, String name) {
		super(Status.BAD_REQUEST, id, name, "still has components attached");
	}
}
