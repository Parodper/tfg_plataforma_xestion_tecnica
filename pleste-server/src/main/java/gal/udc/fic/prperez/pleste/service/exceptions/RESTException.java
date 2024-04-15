package gal.udc.fic.prperez.pleste.service.exceptions;

import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

public class RESTException extends WebApplicationException {
	public RESTException(String message, Response.Status status) {
		super(
				Response.status(status)
						.entity("{ \"message\": \"" + message + "\" }")
						.type(MediaType.APPLICATION_JSON)
						.build()
		);
	}
}
