package gal.udc.fic.prperez.pleste.test.service.template;

import gal.udc.fic.prperez.pleste.service.*;
import gal.udc.fic.prperez.pleste.service.dao.SQLDaoFactoryUtil;
import gal.udc.fic.prperez.pleste.service.dao.template.Template;
import gal.udc.fic.prperez.pleste.service.exceptions.ParseSearchException;
import jakarta.persistence.EntityManagerFactory;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(classes = Application.class)
public class SearchResourceTest {
	private final SearchResource searchResource;
	private final TemplateResource templateResource;
	private final ComponentResource componentResource;
	private final UsersResource usersResource;

	@Autowired
	public SearchResourceTest(SearchResource searchResource,
	                          TemplateResource templateResource,
	                          ComponentResource componentResource,
	                          UsersResource usersResource) {
		this.searchResource = searchResource;
		this.templateResource = templateResource;
		this.componentResource = componentResource;
		this.usersResource = usersResource;
	}

	private static Template createTestTemplate(String suffix) {
		Template template = new Template();
		template.setName("name" + suffix);
		template.setDescription("Description " + suffix);

		return template;
	}

	@Test
	public void searchTemplates() {
		Long idA = templateResource.addTemplate(createTestTemplate("AA"));
		Long idB = templateResource.addTemplate(createTestTemplate("BA"));

		assertEquals(searchResource.searchTemplates("(template.name ~ \"y\" & template.name @ [ \"A\" ])", null, null).size(),0);
		assertEquals(searchResource.searchTemplates("template.name ~ \"name[AB]A\"",null, null).size(), 2);
		assertEquals(searchResource.searchTemplates("template.name ~ \".*\"", 3, 2).size(), 2);
		assertEquals(searchResource.searchTemplates("template.name ~ \".*\"", 4, 2).size(), 1);

		assertThrows(ParseSearchException.class, () -> searchResource.searchTemplates("template.name ~ \"y & template.name > 2",null, null));
		assertThrows(ParseSearchException.class, () -> searchResource.searchTemplates("template.name ~ & template.name > 2",null, null));
		assertThrows(ParseSearchException.class, () -> searchResource.searchTemplates("(component.name ~ 2",null, null));

		templateResource.removeTemplate(idA.toString());
		templateResource.removeTemplate(idB.toString());
	}

	@Test
	public void searchUsers() {
		assertEquals(searchResource.searchUsers("user.name @ [\"root\", \" 12 \"] | user.role @ [ \"NORMAL\" ]",null, null).size(),1);
		assertThrows(ParseSearchException.class, () -> searchResource.searchUsers("x > 2 & (y<= 3& z != 4) |a = -3.4 | m ~ \"x |x ~x\"",null, null));
	}

	@Test
	public void searchComponents() {
		assertEquals(searchResource.searchComponents("component.field.type @ [ \"LINK\" ] ",null, null).size(),0);
		assertEquals(searchResource.searchComponents("component.field.value > 2 | component.field.value = 2001-01-01T00:00:00",null, null).size(),0);
	}
}
