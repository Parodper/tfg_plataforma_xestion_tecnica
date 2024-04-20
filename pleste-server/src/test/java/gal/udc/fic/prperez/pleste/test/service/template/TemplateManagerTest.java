package gal.udc.fic.prperez.pleste.test.service.template;

import gal.udc.fic.prperez.pleste.service.Application;
import gal.udc.fic.prperez.pleste.service.TemplateResource;
import gal.udc.fic.prperez.pleste.service.dao.template.Template;
import gal.udc.fic.prperez.pleste.service.exceptions.template.TemplateAlreadyExistsException;
import gal.udc.fic.prperez.pleste.service.exceptions.template.TemplateNotFoundException;
import gal.udc.fic.prperez.pleste.service.exceptions.template.TemplateStillInUseException;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest(classes = Application.class)
@TestPropertySource(properties="spring.jpa.properties.hibernate.default_schema=testing")
@TestPropertySource(properties="spring.jpa.hibernate.ddl-auto=create-drop")
public class TemplateManagerTest {
	@Autowired
	TemplateResource templateResource;
	
	private static Template createTestTemplate(String suffix) {
		Template template = new Template();
		template.setName("name" + suffix);
		template.setDescription("Description " + suffix);
		
		return template;
	}
	
	private static Template createTestTemplate() {
		return createTestTemplate("");
	}

	@Test
	public void createTemplateTest() throws TemplateAlreadyExistsException, TemplateNotFoundException, TemplateStillInUseException {
		Long id;

		id = templateResource.addTemplate(createTestTemplate());
		assertEquals(templateResource.getTemplate(id.toString()).getName(), "name");

		templateResource.removeTemplate(id.toString());
	}

	@Test
	public void deleteTemplateTest() throws TemplateAlreadyExistsException, TemplateNotFoundException, TemplateStillInUseException {
		Long id = templateResource.addTemplate(createTestTemplate());
		assertEquals("name", templateResource.getTemplate(id.toString()).getName());
		templateResource.removeTemplate(id.toString());
		assertThrows(TemplateNotFoundException.class, () -> templateResource.getTemplate(id.toString()));
	}

	@Test
	public void modifyTemplateTest() throws TemplateAlreadyExistsException, TemplateNotFoundException, TemplateStillInUseException {
		Long idA, idB;
		Template newTemplate;

		idA = templateResource.addTemplate(createTestTemplate());
		idB = templateResource.addTemplate(createTestTemplate("B"));

		newTemplate = templateResource.getTemplate(idB.toString());

		newTemplate.setName("name");
		templateResource.modifyTemplate(idB.toString(), newTemplate);

		assertEquals(2, templateResource.getTemplatesByName("name").size());

		templateResource.removeTemplate(idA.toString());
		templateResource.removeTemplate(idB.toString());
	}
}
