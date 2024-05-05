package gal.udc.fic.prperez.pleste.client.exceptions;

public class AccessDeniedException extends RuntimeException {
	public AccessDeniedException() {
		super("Current user can't access this resource");
	}
}
