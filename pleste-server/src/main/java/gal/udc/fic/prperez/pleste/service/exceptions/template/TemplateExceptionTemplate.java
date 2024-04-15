package gal.udc.fic.prperez.pleste.service.exceptions.template;

import gal.udc.fic.prperez.pleste.service.exceptions.RESTException;
import jakarta.ws.rs.core.Response;

public class TemplateExceptionTemplate extends RESTException {
	public TemplateExceptionTemplate(Response.Status statusCode, String id, String name, String reason) {
		super("Template " + id + " (" + name + ") " + reason, statusCode);
	}
}
