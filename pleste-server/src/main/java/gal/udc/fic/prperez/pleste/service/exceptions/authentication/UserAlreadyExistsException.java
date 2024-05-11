package gal.udc.fic.prperez.pleste.service.exceptions.authentication;

import gal.udc.fic.prperez.pleste.service.exceptions.RESTException;
import jakarta.ws.rs.core.Response.Status;

public class UserAlreadyExistsException extends RESTException {
	public UserAlreadyExistsException(String user) {
		super("User " + user + " already exists", Status.BAD_REQUEST);
	}
}
