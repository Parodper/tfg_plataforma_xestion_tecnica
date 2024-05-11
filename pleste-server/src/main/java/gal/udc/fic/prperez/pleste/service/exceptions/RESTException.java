package gal.udc.fic.prperez.pleste.service.exceptions;

import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.Response.Status;

public class RESTException extends WebApplicationException {
	private final Status status;
	private final String message;

	public RESTException(String message, Status status) {
		super(message, status);
		this.message = message;
		this.status = status;
	}

	public Status getStatus() {
		return status;
	}

	public String getMessage() {
		return message;
	}
}
