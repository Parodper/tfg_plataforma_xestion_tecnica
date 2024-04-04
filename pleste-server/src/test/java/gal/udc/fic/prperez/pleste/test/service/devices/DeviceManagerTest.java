package gal.udc.fic.prperez.pleste.test.service.devices;

import gal.udc.fic.prperez.pleste.service.Application;
import gal.udc.fic.prperez.pleste.service.devices.DeviceManager;
import gal.udc.fic.prperez.pleste.service.devices.exceptions.*;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest(classes = Application.class)
public class DeviceManagerTest {
	@Autowired
	DeviceManager deviceManager;

	@BeforeAll
	public static void setup() {
	}

	@Test
	public void createDeviceTest() throws DeviceAlreadyExistsException, DeviceNotFoundException {
		Long id;

		id = deviceManager.addDevice("name", "location");
		assertEquals(deviceManager.getDevice(id).getName(), "name");

		deviceManager.removeDevice(id);
	}
}
