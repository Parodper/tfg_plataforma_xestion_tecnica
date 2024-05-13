package gal.udc.fic.prperez.pleste.service.exceptions;

import jakarta.ws.rs.core.Response.Status;

public class RESTException extends RuntimeException {
	private final Integer status;
	private final String message;

	public RESTException(String message, Integer status) {
		this.message = message;
		this.status = status;
	}

	public RESTException(String message, Status status) {
		this(message, status.getStatusCode());
	}

	public Integer getStatus() {
		return status;
	}

	public String getMessage() {
		return message;
	}
}
