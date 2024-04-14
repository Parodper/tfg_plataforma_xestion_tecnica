package gal.udc.fic.prperez.pleste.service.model.exceptions;

public class TemplateAlreadyExistsException extends RuntimeException {
	public TemplateAlreadyExistsException(Long id, String name) {
		super("Device " + id.toString() + " (" + name + ") already exists");
	}
}
