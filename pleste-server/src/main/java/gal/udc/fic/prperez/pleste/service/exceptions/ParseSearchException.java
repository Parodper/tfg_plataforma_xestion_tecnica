package gal.udc.fic.prperez.pleste.service.exceptions;

import jakarta.ws.rs.core.Response;

public class ParseSearchException extends RESTException {
	public ParseSearchException(String message) {
		super(message, Response.Status.BAD_REQUEST);
	}
}
