package gal.udc.fic.prperez.pleste.test.service.template;

import gal.udc.fic.prperez.pleste.service.Application;
import gal.udc.fic.prperez.pleste.service.ComponentResource;
import gal.udc.fic.prperez.pleste.service.TemplateResource;
import gal.udc.fic.prperez.pleste.service.dao.component.Component;
import gal.udc.fic.prperez.pleste.service.dao.component.Field;
import gal.udc.fic.prperez.pleste.service.dao.template.FieldTypes;
import gal.udc.fic.prperez.pleste.service.dao.template.Template;
import gal.udc.fic.prperez.pleste.service.dao.template.TemplateField;
import gal.udc.fic.prperez.pleste.service.exceptions.component.ComponentFieldIsMandatoryException;
import gal.udc.fic.prperez.pleste.service.exceptions.component.ComponentNotFoundException;
import gal.udc.fic.prperez.pleste.service.exceptions.template.TemplateAlreadyExistsException;
import gal.udc.fic.prperez.pleste.service.exceptions.template.TemplateNotFoundException;
import gal.udc.fic.prperez.pleste.service.exceptions.template.TemplateStillInUseException;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

import java.time.LocalDateTime;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(classes = Application.class)
@TestPropertySource(properties="spring.jpa.properties.hibernate.default_schema=testing")
@TestPropertySource(properties="spring.jpa.hibernate.ddl-auto=create-drop")
public class ComponentResourceTest {
	private final ComponentResource componentResource;
	private final TemplateResource templateResource;

	public @Autowired  ComponentResourceTest(ComponentResource componentResource, TemplateResource templateResource) {
		this.componentResource = componentResource;
		this.templateResource = templateResource;
	}

	private static Template testTemplate;

	private static Component createTestComponent(String suffix) {
		Field field;
		Component component = new Component();

		component.setName("Q_" + suffix);
		component.setTemplate(testTemplate);
		component.setDescription("Description" + suffix);
		component.setFields(new ArrayList<>());
		field = new Field();
		field.setName("X");
		field.setContent(LocalDateTime.now().toString());
		field.setTemplateField(testTemplate.getFields().get(1));
		component.getFields().add(field);
		field = new Field();
		field.setName("Y");
		field.setLink(null);
		field.setTemplateField(testTemplate.getFields().get(2));
		component.getFields().add(field);
		field = new Field();
		field.setName("Z");
		field.setContent("text");
		field.setTemplateField(testTemplate.getFields().get(0));
		component.getFields().add(field);

		return component;
	}

	@BeforeEach
	public void setUp() {
		String id;
		Template template = new Template();
		template.setName("name2");
		template.setDescription("Description");
		id = templateResource.addTemplate(template).toString();
		testTemplate = templateResource.getTemplate(id);

		testTemplate.setFields(new ArrayList<>());
		testTemplate.getFields().add(new TemplateField(null, "X", true, FieldTypes.TEXT));
		testTemplate.getFields().add(new TemplateField(null, "Y", false, FieldTypes.DATETIME));
		testTemplate.getFields().add(new TemplateField(null, "Z", false, FieldTypes.LINK));
		templateResource.modifyTemplate(id, testTemplate);
		testTemplate = templateResource.getTemplate(id);
	}

	@AfterEach
	public void tearDown() {
		templateResource.removeTemplate(testTemplate.getId().toString());
		testTemplate = null;
	}

	@Test
	public void createComponentTest() {
		String id;
		Component component = new Component();

		component.setName("Q");
		component.setDescription("Description");
		component.setTemplate(testTemplate);
		component.setFields(new ArrayList<>());
		component.getFields().add(new Field(null, "Z", "text", testTemplate.getFields().get(1), null));
		component.getFields().add(new Field(null, "Y", null, testTemplate.getFields().get(2), null));
		assertThrows(ComponentFieldIsMandatoryException.class, () -> componentResource.addComponent(component));
		component.getFields().set(2, new Field(null, "X", LocalDateTime.now().toString(), testTemplate.getFields().get(0), null));

		id = componentResource.addComponent(component).toString();

		assertEquals(componentResource.getComponent(id).getName(), "Q");

		componentResource.removeComponent(id);
	}

	@Test
	public void deleteComponentTest() {
		String id = componentResource.addComponent(createTestComponent("")).toString();
		assertEquals("Q_", componentResource.getComponent(id).getName());
		componentResource.removeComponent(id);
		assertThrows(ComponentNotFoundException.class, () -> componentResource.getComponent(id));
	}

	@Test
	public void modifyComponentTest() throws TemplateAlreadyExistsException, TemplateNotFoundException, TemplateStillInUseException {
		Component component = createTestComponent("modify");
		String id = componentResource.addComponent(component).toString();

		assertThrows(ComponentFieldIsMandatoryException.class, () -> componentResource.modifyFieldComponent(id, "X", null));

		Component tmp = componentResource.getComponent(id);
		tmp.setName("New name");
		componentResource.modifyComponent(id, tmp);
		assertEquals(componentResource.getComponent(id).getName(), "New name");

		assertEquals(componentResource.getFieldsComponent(id).size(), 3);
		componentResource.removeComponentField(id, "Y");
		assertNull(componentResource.getFieldComponent(id, "Y").getLink());
		assertEquals(componentResource.getFieldsComponent(id).size(), 3);

		componentResource.removeComponent(id);
	}
}
