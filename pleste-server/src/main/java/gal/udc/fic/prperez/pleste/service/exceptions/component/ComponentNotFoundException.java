package gal.udc.fic.prperez.pleste.service.exceptions.component;

import jakarta.ws.rs.core.Response.Status;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class ComponentNotFoundException extends ComponentExceptionTemplate {
	public ComponentNotFoundException(String id, String name) {
		super(Status.NOT_FOUND, id, name, "not found");
	}
}
