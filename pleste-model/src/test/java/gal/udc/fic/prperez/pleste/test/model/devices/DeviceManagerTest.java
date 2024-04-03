package gal.udc.fic.prperez.pleste.test.model.devices;

import gal.udc.fic.prperez.pleste.model.devices.Device;
import gal.udc.fic.prperez.pleste.model.devices.DeviceManager;
import gal.udc.fic.prperez.pleste.model.devices.DeviceManagerFactory;
import gal.udc.fic.prperez.pleste.model.devices.exceptions.DeviceAlreadyExistsException;
import gal.udc.fic.prperez.pleste.model.devices.exceptions.DeviceNotFoundException;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class DeviceManagerTest {
	static DeviceManager deviceManager;

	@BeforeAll
	public static void setup() {
		deviceManager = DeviceManagerFactory.getService();
	}

	@Disabled
	public void createDeviceTest() throws DeviceAlreadyExistsException, DeviceNotFoundException {
		Device d = new Device(Device.INVALID_ID, "", "name", "location", "owner");
		Long id;

		id = deviceManager.addDevice(d);
		assertEquals(deviceManager.getDevice(0L).getName(), "name");

		deviceManager.removeDevice(id);
	}
}
