package gal.udc.fic.prperez.pleste.service.exceptions.template;

public class TemplateAlreadyExistsException extends TemplateExceptionTemplate {
	public TemplateAlreadyExistsException(Long id, String name) {
		super(id.toString(), name, "already exists");
	}
}
