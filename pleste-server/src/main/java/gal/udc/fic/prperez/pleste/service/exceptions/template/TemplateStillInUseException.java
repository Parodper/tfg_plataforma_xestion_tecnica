package gal.udc.fic.prperez.pleste.service.exceptions.template;

public class TemplateStillInUseException extends TemplateExceptionTemplate {
	public TemplateStillInUseException(Long id, String name) {
		super(id.toString(), name, "still has component attached");
	}
}
