package gal.udc.fic.prperez.pleste.service.exceptions.component;

import jakarta.ws.rs.core.Response;

public class ComponentMissingFieldException extends ComponentExceptionTemplate {
	public ComponentMissingFieldException(String name, String reason) {
		super(Response.Status.BAD_REQUEST, null, name, "needs the following fields present in the template (even if empty): " + reason);
	}
}
