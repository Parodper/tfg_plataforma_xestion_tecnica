package gal.udc.fic.prperez.pleste.service.exceptions.component;

import gal.udc.fic.prperez.pleste.service.exceptions.RESTException;
import jakarta.ws.rs.core.Response;

public class ComponentExceptionTemplate extends RESTException {
	public ComponentExceptionTemplate(Response.Status statusCode, String id, String name, String reason) {
		super("Component " + id +
				(name != null ? " (" + name + ") " : "") +
				reason, statusCode);
	}
}
