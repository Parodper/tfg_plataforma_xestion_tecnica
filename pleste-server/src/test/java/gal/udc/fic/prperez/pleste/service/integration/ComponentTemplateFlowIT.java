package gal.udc.fic.prperez.pleste.service.integration;

import gal.udc.fic.prperez.pleste.service.Application;
import gal.udc.fic.prperez.pleste.service.ComponentResource;
import gal.udc.fic.prperez.pleste.service.JSONDatetime;
import gal.udc.fic.prperez.pleste.service.TemplateResource;
import gal.udc.fic.prperez.pleste.service.dao.component.Component;
import gal.udc.fic.prperez.pleste.service.dao.component.DatetimeField;
import gal.udc.fic.prperez.pleste.service.dao.component.Field;
import gal.udc.fic.prperez.pleste.service.dao.component.TextField;
import gal.udc.fic.prperez.pleste.service.dao.template.FieldTypes;
import gal.udc.fic.prperez.pleste.service.dao.template.Template;
import gal.udc.fic.prperez.pleste.service.dao.template.TemplateField;
import gal.udc.fic.prperez.pleste.service.exceptions.component.ComponentFieldIsMandatoryException;
import gal.udc.fic.prperez.pleste.service.exceptions.component.ComponentNotFoundException;
import gal.udc.fic.prperez.pleste.service.exceptions.template.TemplateNotFoundException;
import gal.udc.fic.prperez.pleste.service.exceptions.template.TemplateStillInUseException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(classes = Application.class)
@DisplayName("Integration Tests: Template to Component Lifecycle Flow")
public class ComponentTemplateFlowIT {

	@Autowired
	private ComponentResource componentResource;

	@Autowired
	private TemplateResource templateResource;

	private Long createdTemplateId;
	private Template createdTemplate;
	private final List<Long> createdComponentIds = new ArrayList<>();

	@BeforeEach
	void setUp() {
		Template template = new Template();
		template.setName("FlowTemplate_" + System.nanoTime());
		template.setDescription("Template for flow testing");

		List<TemplateField> fields = new ArrayList<>();
		fields.add(new TemplateField(null, "serialNumber", true, FieldTypes.TEXT));
		fields.add(new TemplateField(null, "installationDate", false, FieldTypes.DATETIME));
		template.setFields(fields);

		createdTemplateId = templateResource.addTemplate(template);
		createdTemplate = templateResource.getTemplate(createdTemplateId.toString());
	}

	@AfterEach
	void tearDown() {
		for (Long compId : createdComponentIds) {
			try {
				componentResource.removeComponent(compId.toString());
			} catch (Exception ignored) {
			}
		}
		createdComponentIds.clear();

		if (createdTemplateId != null) {
			try {
				templateResource.removeTemplate(createdTemplateId.toString());
			} catch (Exception ignored) {
			}
		}
	}

	@Test
	@DisplayName("Create component based on template returns positive generated ID")
	void testCreateComponentFromTemplateReturnsValidId() {
		Component component = new Component();
		component.setName("PressureUnit_01");
		component.setDescription("Primary pressure sensor");
		component.setTemplate(createdTemplate);

		List<Field<?>> fields = new ArrayList<>();
		fields.add(new TextField(null, "SN-12345", createdTemplate.getFields().get(0)));
		fields.add(new DatetimeField(null, JSONDatetime.now(), createdTemplate.getFields().get(1)));
		component.setFields(fields);

		Long compId = componentResource.addComponent(component);
		createdComponentIds.add(compId);

		assertTrue(compId > 0);
	}

	@Test
	@DisplayName("Create component persists name correctly in database")
	void testCreateComponentPersistsName() {
		Component component = new Component();
		component.setName("TempSensor_02");
		component.setDescription("Secondary temp sensor");
		component.setTemplate(createdTemplate);

		List<Field<?>> fields = new ArrayList<>();
		fields.add(new TextField(null, "SN-99999", createdTemplate.getFields().get(0)));
		component.setFields(fields);

		Long compId = componentResource.addComponent(component);
		createdComponentIds.add(compId);

		Component fetched = componentResource.getComponent(compId.toString());
		assertEquals("TempSensor_02", fetched.getName());
	}

	@Test
	@DisplayName("Create component without mandatory field throws ComponentFieldIsMandatoryException")
	void testCreateComponentMissingMandatoryFieldThrows() {
		Component component = new Component();
		component.setName("Incomplete_Unit");
		component.setTemplate(createdTemplate);
		component.setFields(new ArrayList<>());

		assertThrows(ComponentFieldIsMandatoryException.class,
				() -> componentResource.addComponent(component));
	}

	@Test
	@DisplayName("Create component referencing non-existent template throws TemplateNotFoundException")
	void testCreateComponentNonExistentTemplateThrows() {
		Component component = new Component();
		component.setName("Orphan_Unit");
		component.setTemplate(new Template(999999L));
		component.setFields(new ArrayList<>());

		assertThrows(TemplateNotFoundException.class,
				() -> componentResource.addComponent(component));
	}

	@Test
	@DisplayName("Create component auto-populates optional fields from template")
	void testCreateComponentAutoPopulatesOptionalFields() {
		Component component = new Component();
		component.setName("AutoPopulated_Unit");
		component.setTemplate(createdTemplate);

		List<Field<?>> fields = new ArrayList<>();
		fields.add(new TextField(null, "SN-55555", createdTemplate.getFields().get(0)));
		component.setFields(fields);

		Long compId = componentResource.addComponent(component);
		createdComponentIds.add(compId);

		List<Field> compFields = componentResource.getFieldsComponent(compId.toString());
		assertEquals(2, compFields.size());
	}

	@Test
	@DisplayName("Delete component removes it from persistence")
	void testDeleteComponentRemovesIt() {
		Component component = new Component();
		component.setName("UnitToDelete");
		component.setTemplate(createdTemplate);

		List<Field<?>> fields = new ArrayList<>();
		fields.add(new TextField(null, "SN-DELETE", createdTemplate.getFields().get(0)));
		component.setFields(fields);

		Long compId = componentResource.addComponent(component);
		componentResource.removeComponent(compId.toString());

		assertThrows(ComponentNotFoundException.class,
				() -> componentResource.getComponent(compId.toString()));
	}

	@Test
	@DisplayName("Delete template fails with TemplateStillInUseException when components exist")
	void testDeleteTemplateWithComponentsFails() {
		Component component = new Component();
		component.setName("ActiveComponent");
		component.setTemplate(createdTemplate);

		List<Field<?>> fields = new ArrayList<>();
		fields.add(new TextField(null, "SN-ACTIVE", createdTemplate.getFields().get(0)));
		component.setFields(fields);

		Long compId = componentResource.addComponent(component);
		createdComponentIds.add(compId);

		assertThrows(TemplateStillInUseException.class,
				() -> templateResource.removeTemplate(createdTemplateId.toString()));
	}

	@Test
	@DisplayName("Get template components returns all components created from that template")
	void testGetTemplateComponentsCount() {
		Component c1 = new Component();
		c1.setName("Unit_A");
		c1.setTemplate(createdTemplate);
		c1.setFields(new ArrayList<>(List.of(new TextField(null, "SN-A", createdTemplate.getFields().get(0)))));

		Component c2 = new Component();
		c2.setName("Unit_B");
		c2.setTemplate(createdTemplate);
		c2.setFields(new ArrayList<>(List.of(new TextField(null, "SN-B", createdTemplate.getFields().get(0)))));

		createdComponentIds.add(componentResource.addComponent(c1));
		createdComponentIds.add(componentResource.addComponent(c2));

		List<Component> list = templateResource.getTemplateComponents(createdTemplateId.toString());
		assertEquals(2, list.size());
	}

	@Test
	@DisplayName("Modify component updates name in database")
	void testModifyComponentName() {
		Component component = new Component();
		component.setName("InitialName");
		component.setDescription("InitialDesc");
		component.setTemplate(createdTemplate);

		List<Field<?>> fields = new ArrayList<>();
		fields.add(new TextField(null, "SN-MODIFY", createdTemplate.getFields().get(0)));
		component.setFields(fields);

		Long compId = componentResource.addComponent(component);
		createdComponentIds.add(compId);

		Component toUpdate = componentResource.getComponent(compId.toString());
		toUpdate.setName("UpdatedName");
		componentResource.modifyComponent(compId.toString(), toUpdate);

		Component updated = componentResource.getComponent(compId.toString());
		assertEquals("UpdatedName", updated.getName());
	}
}
