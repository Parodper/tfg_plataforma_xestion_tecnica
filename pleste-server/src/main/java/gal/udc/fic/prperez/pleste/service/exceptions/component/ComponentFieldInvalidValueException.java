package gal.udc.fic.prperez.pleste.service.exceptions.component;

import gal.udc.fic.prperez.pleste.service.exceptions.RESTException;
import jakarta.ws.rs.core.Response.Status;

public class ComponentFieldInvalidValueException extends RESTException {
	public ComponentFieldInvalidValueException(String name, String type, String value) {
		super("Value " + value + " can't be entered on field " + name + " (" + type + ").", Status.BAD_REQUEST);
	}
}
