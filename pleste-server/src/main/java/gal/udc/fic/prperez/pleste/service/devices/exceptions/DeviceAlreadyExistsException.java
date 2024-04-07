package gal.udc.fic.prperez.pleste.service.devices.exceptions;

public class DeviceAlreadyExistsException extends RuntimeException {
	public DeviceAlreadyExistsException(Long id, String name) {
		super("Device " + id.toString() + " (" + name + ") already exists");
	}
}
