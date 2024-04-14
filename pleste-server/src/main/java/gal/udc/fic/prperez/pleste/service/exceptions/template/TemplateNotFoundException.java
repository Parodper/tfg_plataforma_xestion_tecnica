package gal.udc.fic.prperez.pleste.service.exceptions.template;

public class TemplateNotFoundException extends TemplateExceptionTemplate {
	public TemplateNotFoundException(String id, String name) {
		super(id, name, "not found");
	}
}
