package gal.udc.fic.prperez.pleste.client.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.NOT_FOUND)
public class ObjectAlreadyExistsException extends RuntimeException {
	public ObjectAlreadyExistsException(String object, String username) {
		super(object + " " + username + " xa existe");
	}
}
