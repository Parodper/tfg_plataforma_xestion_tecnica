package gal.udc.fic.prperez.pleste.client.exceptions;

import org.openapitools.client.JSON;
import org.openapitools.client.model.RESTExceptionSerializable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.INTERNAL_SERVER_ERROR)
public class InternalErrorException extends RuntimeException {
	public InternalErrorException(String message) {
		super(((RESTExceptionSerializable) JSON.deserialize(message, RESTExceptionSerializable.class)).getMessage());
	}
}
