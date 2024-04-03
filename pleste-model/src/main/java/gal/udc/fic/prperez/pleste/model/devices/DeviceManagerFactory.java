package gal.udc.fic.prperez.pleste.model.devices;

public class DeviceManagerFactory {
	private static DeviceManager service = null;
	private DeviceManagerFactory() {
	}

	private static DeviceManager getInstance(){
		try {
			return new DeviceManager();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	public synchronized static DeviceManager getService() {
		if (service == null) {
			service = getInstance();
		}
		return service;
	}
}
