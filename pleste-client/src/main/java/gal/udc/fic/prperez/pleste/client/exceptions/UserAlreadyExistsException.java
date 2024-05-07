package gal.udc.fic.prperez.pleste.client.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.NOT_FOUND)
public class UserAlreadyExistsException extends RuntimeException {
	public UserAlreadyExistsException(String username) {
		super("User " + username + " already exists");
	}
}
