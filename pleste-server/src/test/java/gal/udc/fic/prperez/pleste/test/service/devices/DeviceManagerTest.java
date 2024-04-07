package gal.udc.fic.prperez.pleste.test.service.devices;

import gal.udc.fic.prperez.pleste.service.Application;
import gal.udc.fic.prperez.pleste.service.devices.DeviceManager;
import gal.udc.fic.prperez.pleste.service.devices.dao.Device;
import gal.udc.fic.prperez.pleste.service.devices.exceptions.*;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest(classes = Application.class)
@TestPropertySource(properties="spring.jpa.properties.hibernate.default_schema=testing")
@TestPropertySource(properties="spring.jpa.hibernate.ddl-auto=create")
public class DeviceManagerTest {
	@Autowired
	DeviceManager deviceManager;

	@Test
	public void createDeviceTest() throws DeviceAlreadyExistsException, DeviceNotFoundException {
		Long id;

		id = deviceManager.addDevice("name", "location");
		assertEquals(deviceManager.getDevice(id).getName(), "name");

		deviceManager.removeDevice(id);
	}

	@Test
	public void deleteDeviceTest() {
		Long id = deviceManager.addDevice("name", "location");
		assertEquals("name", deviceManager.getDevice(id).getName());
		deviceManager.removeDevice(id);
		assertThrows(DeviceNotFoundException.class, () -> deviceManager.getDevice(id));
	}

	@Test
	public void modifyDeviceTest() {
		Long idA, idB;
		Device newDevice;

		idA = deviceManager.addDevice("name", "location");
		idB = deviceManager.addDevice("name2", "location2");

		newDevice = deviceManager.getDevice(idB);

		newDevice.setName("name");
		deviceManager.modifyDevice(newDevice);

		assertEquals(2, deviceManager.getDevicesByName("name").size());

		deviceManager.removeDevice(idA);
		deviceManager.removeDevice(idB);
	}
}
