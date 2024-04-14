package gal.udc.fic.prperez.pleste.service.exceptions.template;

public class TemplateExceptionTemplate extends RuntimeException {
	public TemplateExceptionTemplate(String id, String name, String reason) {
		super("Template " + id + " (" + name + ") " + reason);
	}
}
