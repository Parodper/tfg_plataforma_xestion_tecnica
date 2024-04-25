package gal.udc.fic.prperez.pleste.service.exceptions.component;

import jakarta.ws.rs.core.Response;

public class ComponentNotFoundException extends ComponentExceptionTemplate {
	public ComponentNotFoundException(String id, String name) {
		super(Response.Status.NOT_FOUND, id, name, "not found");
	}
}
