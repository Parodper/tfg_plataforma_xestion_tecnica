package gal.udc.fic.prperez.pleste.model.devices.exceptions;

public class DeviceNotFoundException extends Exception {
	public DeviceNotFoundException(Long id, String name) {
		super("Device " + id.toString() + " (" + name + ") already exists");
	}
}
