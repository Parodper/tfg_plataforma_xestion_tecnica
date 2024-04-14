package gal.udc.fic.prperez.pleste.service.exceptions;

public class TemplateFieldNotFoundException extends RuntimeException {
	public TemplateFieldNotFoundException(String template, String field) {
		super("Field " + field + " not found in template " + template);
	}
}
