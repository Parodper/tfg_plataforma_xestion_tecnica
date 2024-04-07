package gal.udc.fic.prperez.pleste.service.devices.exceptions;

public class DeviceNotFoundException extends RuntimeException {
	public DeviceNotFoundException(Long id, String name) {
		super("Device " + id.toString() + " (" + name + ") already exists");
	}
}
