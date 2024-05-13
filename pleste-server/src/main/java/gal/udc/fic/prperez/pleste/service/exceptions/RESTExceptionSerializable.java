package gal.udc.fic.prperez.pleste.service.exceptions;

import jakarta.ws.rs.core.Response;

public class RESTExceptionSerializable {
	private final Integer status;
	private final String message;

	public RESTExceptionSerializable(RESTException restException) {
		this.message = restException.getMessage();
		this.status = restException.getStatus();
	}

	public Integer getStatus() {
		return status;
	}

	public String getMessage() {
		return message;
	}
}
