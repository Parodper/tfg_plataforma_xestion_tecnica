package gal.udc.fic.prperez.pleste.service.model.exceptions;

public class TemplateNotFoundException extends RuntimeException {
	public TemplateNotFoundException(Long id, String name) {
		super("Device " + id.toString() + " (" + name + ") already exists");
	}
}
