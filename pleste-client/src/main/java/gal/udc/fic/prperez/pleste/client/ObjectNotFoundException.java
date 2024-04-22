package gal.udc.fic.prperez.pleste.client;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.NOT_FOUND)
public class ObjectNotFoundException extends RuntimeException {
	public ObjectNotFoundException(String message) {
		super(message);
	}
}
