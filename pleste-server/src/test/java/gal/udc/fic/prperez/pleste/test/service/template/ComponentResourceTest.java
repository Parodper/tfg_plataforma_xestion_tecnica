package gal.udc.fic.prperez.pleste.test.service.template;

import gal.udc.fic.prperez.pleste.service.Application;
import gal.udc.fic.prperez.pleste.service.ComponentResource;
import gal.udc.fic.prperez.pleste.service.TemplateResource;
import gal.udc.fic.prperez.pleste.service.dao.component.*;
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

import java.time.LocalDateTime;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(classes = Application.class)
public class ComponentResourceTest {
	private final ComponentResource componentResource;
	private final TemplateResource templateResource;

	public @Autowired  ComponentResourceTest(ComponentResource componentResource, TemplateResource templateResource) {
		this.componentResource = componentResource;
		this.templateResource = templateResource;
	}

	private static Template testTemplate;

	private static Component createTestComponent(String suffix) {
		Component component = new Component();

		component.setName("Q_" + suffix);
		component.setTemplate(testTemplate);
		component.setDescription("Description" + suffix);
		component.setFields(new ArrayList<>());
		var field1 = new DatetimeField();
		field1.setContent(LocalDateTime.now());
		field1.setTemplateField(testTemplate.getFields().get(1));
		component.getFields().add(field1);
		var field2 = new LinkField();
		field2.setContent(null);
		field2.setTemplateField(testTemplate.getFields().get(2));
		component.getFields().add(field2);
		var field0 = new TextField();
		field0.setContent("text");
		field0.setTemplateField(testTemplate.getFields().get(0));
		component.getFields().add(field0);

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
		component.getFields().add(new DatetimeField(null, LocalDateTime.now(), testTemplate.getFields().get(1)));
		component.getFields().add(new LinkField(null, null, testTemplate.getFields().get(2)));
		assertThrows(ComponentFieldIsMandatoryException.class, () -> componentResource.addComponent(component));
		component.getFields().set(2, new TextField(null, "text", testTemplate.getFields().get(0)));

		id = componentResource.addComponent(component).toString();

		assertEquals(componentResource.getComponent(id).getName(), "Q");

		componentResource.removeComponent(id);
	}

	@Test
	public void deleteComponentTest() {
		String id = componentResource.addComponent(createTestComponent("delete")).toString();
		assertEquals("Q_delete", componentResource.getComponent(id).getName());
		componentResource.removeComponent(id);
		assertThrows(ComponentNotFoundException.class, () -> componentResource.getComponent(id));
	}

	@Test
	public void modifyComponentTest() throws TemplateAlreadyExistsException, TemplateNotFoundException, TemplateStillInUseException {
		Component component = createTestComponent("modify");
		String id = componentResource.addComponent(component).toString();

		assertThrows(ComponentFieldIsMandatoryException.class, () -> componentResource.modifyDateFieldComponent(id, "X", null));

		Component tmp = componentResource.getComponent(id);
		tmp.setName("New name");
		componentResource.modifyComponent(id, tmp);
		assertEquals(componentResource.getComponent(id).getName(), "New name");

		assertEquals(componentResource.getFieldsComponent(id).size(), 3);
		componentResource.removeComponentField(id, "Y");
		assertNull(componentResource.getFieldComponent(id, "Y").getContent());
		assertEquals(componentResource.getFieldsComponent(id).size(), 3);

		componentResource.removeComponent(id);
	}
}
