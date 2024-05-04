package gal.udc.fic.prperez.pleste.service.exceptions.authentication;

import gal.udc.fic.prperez.pleste.service.exceptions.RESTException;
import jakarta.ws.rs.core.Response;

public class IllegalActionForUserException extends RESTException {
	public IllegalActionForUserException(String user) {
		super("User " + user + " doesn't have enough permissions to do that.", Response.Status.UNAUTHORIZED);
	}
}
