package gal.udc.fic.prperez.pleste.test.model.devices;

import gal.udc.fic.prperez.pleste.model.devices.Device;
import gal.udc.fic.prperez.pleste.model.devices.DeviceManager;
import gal.udc.fic.prperez.pleste.model.devices.DeviceManagerFactory;
import gal.udc.fic.prperez.pleste.model.devices.exceptions.DeviceAlreadyExistsException;
import gal.udc.fic.prperez.pleste.model.devices.exceptions.DeviceNotFoundException;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;

public class DeviceManagerTest {
	static DeviceManager deviceManager;

	@BeforeAll
	public static void setup() {
		deviceManager = DeviceManagerFactory.getService();
	}

	@Test
	public void createDeviceTest() throws DeviceAlreadyExistsException, DeviceNotFoundException {
		Device d = new Device(Device.INVALID_ID, "", "name", "location", "owner", new ArrayList<>());
		Long id;

		id = deviceManager.addDevice(d);
		assertEquals(deviceManager.getDevice(0L).getName(), "name");

		deviceManager.removeDevice(id);
	}
}
