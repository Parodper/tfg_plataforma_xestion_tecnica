package gal.udc.fic.prperez.pleste.service.exceptions;

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
