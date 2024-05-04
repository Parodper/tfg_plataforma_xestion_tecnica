package gal.udc.fic.prperez.pleste.service.exceptions.authentication;

import gal.udc.fic.prperez.pleste.service.exceptions.RESTException;
import jakarta.ws.rs.core.Response;

public class UserNotFoundException extends RESTException {
	public UserNotFoundException(String username) {
		super("User " + username + " wasn't found", Response.Status.BAD_REQUEST);
	}
}
