package gal.udc.fic.prperez.pleste.test.service.devices;

import gal.udc.fic.prperez.pleste.service.Application;
import gal.udc.fic.prperez.pleste.service.model.TemplateManager;
import gal.udc.fic.prperez.pleste.service.dao.Device;
import gal.udc.fic.prperez.pleste.service.model.exceptions.*;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest(classes = Application.class)
@TestPropertySource(properties="spring.jpa.properties.hibernate.default_schema=testing")
@TestPropertySource(properties="spring.jpa.hibernate.ddl-auto=create")
public class TemplateManagerTest {
	@Autowired
	TemplateManager templateManager;

	@Test
	public void createDeviceTest() throws TemplateAlreadyExistsException, TemplateNotFoundException {
		Long id;

		id = templateManager.addDevice("name", "location");
		assertEquals(templateManager.getDevice(id).getName(), "name");

		templateManager.removeDevice(id);
	}

	@Test
	public void deleteDeviceTest() {
		Long id = templateManager.addDevice("name", "location");
		assertEquals("name", templateManager.getDevice(id).getName());
		templateManager.removeDevice(id);
		assertThrows(TemplateNotFoundException.class, () -> templateManager.getDevice(id));
	}

	@Test
	public void modifyDeviceTest() {
		Long idA, idB;
		Device newDevice;

		idA = templateManager.addDevice("name", "location");
		idB = templateManager.addDevice("name2", "location2");

		newDevice = templateManager.getDevice(idB);

		newDevice.setName("name");
		templateManager.modifyDevice(newDevice);

		assertEquals(2, templateManager.getDevicesByName("name").size());

		templateManager.removeDevice(idA);
		templateManager.removeDevice(idB);
	}
}
