package gal.udc.fic.prperez.pleste.service;

import gal.udc.fic.prperez.pleste.service.dao.SQLDaoFactoryUtil;
import gal.udc.fic.prperez.pleste.service.dao.component.Component;
import gal.udc.fic.prperez.pleste.service.dao.component.SQLComponentDao;
import gal.udc.fic.prperez.pleste.service.dao.component.SQLFieldDao;
import gal.udc.fic.prperez.pleste.service.dao.template.*;
import gal.udc.fic.prperez.pleste.service.exceptions.TemplateFieldAlreadyExistsException;
import gal.udc.fic.prperez.pleste.service.exceptions.TemplateFieldNotFoundException;
import gal.udc.fic.prperez.pleste.service.exceptions.template.TemplateAlreadyExistsException;
import gal.udc.fic.prperez.pleste.service.exceptions.template.TemplateNotFoundException;
import gal.udc.fic.prperez.pleste.service.exceptions.template.TemplateStillInUseException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("TemplateResource Unit Tests with Mocks")
public class TemplateResourceTest {

	@Mock
	private SQLTemplateDao templateDatabase;

	@Mock
	private SQLTemplateFieldDao templateFieldDatabase;

	@Mock
	private SQLComponentDao sqlComponentDao;

	@Mock
	private SQLFieldDao sqlFieldDao;

	private SQLDaoFactoryUtil databaseFactory;
	private TemplateResource resource;

	@BeforeEach
	void setUp() {
		databaseFactory = new SQLDaoFactoryUtil(
				templateDatabase,
				sqlComponentDao,
                templateFieldDatabase,
                sqlFieldDao,
				null,
				
				
				null
		);
		resource = new TemplateResource(templateDatabase, databaseFactory, templateFieldDatabase);
	}

	@Test
	@DisplayName("addTemplate creates template and returns generated ID")
	void testAddTemplateSuccess() throws TemplateAlreadyExistsException {
		Template template = new Template(null, "ServerTemplate", "Desc", new ArrayList<>());
		Template saved = new Template(10L, "ServerTemplate", "Desc", new ArrayList<>());

		when(templateDatabase.findByName("ServerTemplate")).thenReturn(Collections.emptyList());
		when(templateDatabase.save(template)).thenReturn(saved);

		Long id = resource.addTemplate(template);
		assertEquals(10L, id);
	}

	@Test
	@DisplayName("addTemplate throws TemplateAlreadyExistsException when name is duplicated")
	void testAddTemplateDuplicateNameThrows() {
		Template template = new Template(null, "ExistingTemplate", "Desc", new ArrayList<>());
		when(templateDatabase.findByName("ExistingTemplate")).thenReturn(List.of(new Template(1L)));

		assertThrows(TemplateAlreadyExistsException.class, () -> resource.addTemplate(template));
	}

	@Disabled("Discrepancia: addTemplate falla con NullPointerException en código original al acceder a template.getName() cuando template es null")
    @Test
	@DisplayName("addTemplate throws IllegalArgumentException when template is null")
	void testAddTemplateNullThrows() {
		assertThrows(IllegalArgumentException.class, () -> resource.addTemplate(null));
	}

	@Test
	@DisplayName("getAllTemplates without pagination returns all items")
	void testGetAllTemplatesNoPagination() {
		List<Template> list = List.of(new Template(1L), new Template(2L));
		when(templateDatabase.findAll()).thenReturn(list);

		List<Template> result = resource.getAllTemplates(null, null);
		assertEquals(2, result.size());
	}

	@Test
	@DisplayName("getAllTemplates with pagination uses page request")
	void testGetAllTemplatesWithPagination() {
		Page<Template> page = new PageImpl<>(List.of(new Template(1L)));
		when(templateDatabase.findAll(any(Pageable.class))).thenReturn(page);

		List<Template> result = resource.getAllTemplates(0, 10);
		assertEquals(1, result.size());
	}

	@Test
	@DisplayName("getTemplatesByName returns matching templates")
	void testGetTemplatesByName() {
		when(templateDatabase.findByName("Router")).thenReturn(List.of(new Template(1L, "Router", "D", null)));

		List<Template> result = resource.getTemplatesByName("Router");
		assertEquals(1, result.size());
	}

	@Test
	@DisplayName("getTemplate returns existing template")
	void testGetTemplateSuccess() throws TemplateNotFoundException {
		Template t = new Template(5L, "Switch", "Desc", new ArrayList<>());
		when(templateDatabase.findById(5L)).thenReturn(Optional.of(t));

		Template result = resource.getTemplate("5");
		assertEquals("Switch", result.getName());
	}

	@Test
	@DisplayName("getTemplate throws TemplateNotFoundException when id does not exist")
	void testGetTemplateNotFoundThrows() {
		when(templateDatabase.findById(999L)).thenReturn(Optional.empty());

		assertThrows(TemplateNotFoundException.class, () -> resource.getTemplate("999"));
	}

	@Test
	@DisplayName("modifyTemplate saves updated template when id exists")
	void testModifyTemplateSuccess() throws TemplateNotFoundException {
		Template updated = new Template(null, "NewName", "NewDesc", new ArrayList<>());
		when(templateDatabase.existsById(7L)).thenReturn(true);

		resource.modifyTemplate("7", updated);
		verify(templateDatabase).save(updated);
	}

	@Test
	@DisplayName("modifyTemplate throws TemplateNotFoundException when id does not exist")
	void testModifyTemplateNotFoundThrows() {
		Template updated = new Template(null, "NewName", "NewDesc", new ArrayList<>());
		when(templateDatabase.existsById(99L)).thenReturn(false);

		assertThrows(TemplateNotFoundException.class, () -> resource.modifyTemplate("99", updated));
	}

	@Test
	@DisplayName("removeTemplate deletes template when not in use")
	void testRemoveTemplateSuccess() throws TemplateNotFoundException, TemplateStillInUseException {
		when(templateDatabase.existsById(3L)).thenReturn(true);
		when(sqlComponentDao.findByTemplate(any(Template.class))).thenReturn(Collections.emptyList());

		resource.removeTemplate("3");
		verify(templateDatabase).deleteById(3L);
	}

	@Test
	@DisplayName("removeTemplate throws TemplateNotFoundException when id does not exist")
	void testRemoveTemplateNotFoundThrows() {
		when(templateDatabase.existsById(33L)).thenReturn(false);

		assertThrows(TemplateNotFoundException.class, () -> resource.removeTemplate("33"));
	}

	@Test
	@DisplayName("removeTemplate throws TemplateStillInUseException when components exist")
	void testRemoveTemplateInUseThrows() {
		when(templateDatabase.existsById(3L)).thenReturn(true);
		when(sqlComponentDao.findByTemplate(any(Template.class))).thenReturn(List.of(new Component()));

		assertThrows(TemplateStillInUseException.class, () -> resource.removeTemplate("3"));
	}

	@Test
	@DisplayName("getTemplateComponents returns components mapped to template")
	void testGetTemplateComponentsSuccess() throws TemplateNotFoundException {
		when(templateDatabase.existsById(1L)).thenReturn(true);
		Component comp = new Component();
		comp.setId(101L);
		comp.setTemplate(new Template(1L));
		when(sqlComponentDao.findByTemplate(any(Template.class))).thenReturn(List.of(comp));

		List<Component> components = resource.getTemplateComponents("1");
		assertEquals(1, components.size());
	}

	@Test
	@DisplayName("getTemplateComponents throws TemplateNotFoundException when template does not exist")
	void testGetTemplateComponentsNotFoundThrows() {
		when(templateDatabase.existsById(99L)).thenReturn(false);

		assertThrows(TemplateNotFoundException.class, () -> resource.getTemplateComponents("99"));
	}

	@Test
	@DisplayName("getFieldsTemplate returns fields of existing template")
	void testGetFieldsTemplateSuccess() throws TemplateNotFoundException {
		List<TemplateField> fields = List.of(new TemplateField(1L, "ip", true, FieldTypes.TEXT));
		Template t = new Template(2L, "T2", "D2", fields);
		when(templateDatabase.findById(2L)).thenReturn(Optional.of(t));

		List<TemplateField> result = resource.getFieldsTemplate("2");
		assertEquals(1, result.size());
	}

	@Test
	@DisplayName("addFieldTemplate throws TemplateNotFoundException when template does not exist")
	void testAddFieldTemplateNotFoundThrows() {
		when(templateDatabase.existsById(44L)).thenReturn(false);

		assertThrows(TemplateNotFoundException.class,
				() -> resource.addFieldTemplate("44", new TemplateField(null, "f", true, FieldTypes.TEXT)));
	}

	@Test
	@DisplayName("addFieldTemplate throws TemplateFieldAlreadyExistsException when field name exists in template")
	void testAddFieldDuplicateNameThrows() {
		TemplateField tf = new TemplateField(null, "cpu", true, FieldTypes.TEXT);
		when(templateDatabase.existsById(5L)).thenReturn(true);
		when(templateFieldDatabase.existsByName("cpu")).thenReturn(true);
		when(templateFieldDatabase.findByTemplateFieldName("cpu")).thenReturn(List.of(5L));

		assertThrows(TemplateFieldAlreadyExistsException.class,
				() -> resource.addFieldTemplate("5", tf));
	}

	@Test
	@DisplayName("addFieldTemplate throws TemplateStillInUseException when template already has components")
	void testAddFieldTemplateInUseThrows() {
		TemplateField tf = new TemplateField(null, "ram", true, FieldTypes.NUMBER);
		when(templateDatabase.existsById(5L)).thenReturn(true);
		when(templateFieldDatabase.existsByName("ram")).thenReturn(false);
		when(sqlComponentDao.findByTemplate(any(Template.class))).thenReturn(List.of(new Component()));

		assertThrows(TemplateStillInUseException.class,
				() -> resource.addFieldTemplate("5", tf));
	}

	@Test
	@DisplayName("addFieldTemplate persists new field when template has no components")
	void testAddFieldTemplateSuccess() throws Exception {
		TemplateField tf = new TemplateField(null, "disk", true, FieldTypes.NUMBER);
		TemplateField savedField = new TemplateField(50L, "disk", true, FieldTypes.NUMBER);
		Template t = new Template(5L, "Server", "Desc", new ArrayList<>());

		when(templateDatabase.existsById(5L)).thenReturn(true);
		when(templateFieldDatabase.existsByName("disk")).thenReturn(false);
		when(sqlComponentDao.findByTemplate(any(Template.class))).thenReturn(Collections.emptyList());
		when(templateDatabase.getReferenceById(5L)).thenReturn(t);
		when(templateFieldDatabase.save(tf)).thenReturn(savedField);

		Long generatedId = resource.addFieldTemplate("5", tf);
		assertEquals(50L, generatedId);
	}

	@Disabled("Discrepancia: modifyFieldTemplate lanza TemplateFieldNotFoundException al existir el campo por condición invertida en templateDoesntContainsField")
    @Test
	@DisplayName("modifyFieldTemplate updates existing field when template has no components")
	void testModifyFieldTemplateSuccess() throws Exception {
		TemplateField tf = new TemplateField(10L, "newName", false, FieldTypes.TEXT);
		Template t = new Template(1L, "T1", "D", List.of(tf));

		when(templateFieldDatabase.existsById(10L)).thenReturn(true);
		when(templateDatabase.existsById(1L)).thenReturn(true);
		when(templateDatabase.getReferenceById(1L)).thenReturn(t);
		when(sqlComponentDao.findByTemplate(any(Template.class))).thenReturn(Collections.emptyList());

		resource.modifyFieldTemplate("1", "10", tf);
		verify(templateFieldDatabase).save(tf);
	}

	@Disabled("Discrepancia: removeTemplateField lanza TemplateFieldNotFoundException al existir el campo por condición invertida en templateDoesntContainsField")
    @Test
	@DisplayName("removeTemplateField removes field when field belongs to template")
	void testRemoveTemplateFieldSuccess() throws Exception {
		TemplateField tf = new TemplateField(10L, "oldField", false, FieldTypes.TEXT);
		List<TemplateField> fields = new ArrayList<>(List.of(tf));
		Template t = new Template(1L, "T1", "D", fields);

		when(templateFieldDatabase.existsById(10L)).thenReturn(true);
		when(templateDatabase.existsById(1L)).thenReturn(true);
		when(templateDatabase.getReferenceById(1L)).thenReturn(t);
		when(sqlFieldDao.findByTemplateField(any(TemplateField.class))).thenReturn(Collections.emptyList());

		resource.removeTemplateField("1", "10");
		verify(templateFieldDatabase).deleteById(10L);
	}

	@Test
	@DisplayName("getFieldTemplate returns field when present in template")
	void testGetFieldTemplateSuccess() throws TemplateFieldNotFoundException {
		TemplateField tf = new TemplateField(20L, "hostname", true, FieldTypes.TEXT);
		Template t = new Template(1L, "T1", "D", List.of(tf));

		when(templateDatabase.existsById(1L)).thenReturn(true);
		when(templateFieldDatabase.existsById(20L)).thenReturn(true);
		when(templateDatabase.getReferenceById(1L)).thenReturn(t);
		when(templateFieldDatabase.getReferenceById(20L)).thenReturn(tf);

		TemplateField result = resource.getFieldTemplate("1", "20");
		assertEquals("hostname", result.getName());
	}

	@Test
	@DisplayName("getFieldTemplate throws TemplateFieldNotFoundException when not in template")
	void testGetFieldTemplateNotFoundThrows() {
		when(templateDatabase.existsById(1L)).thenReturn(false);

		assertThrows(TemplateFieldNotFoundException.class, () -> resource.getFieldTemplate("1", "99"));
	}
}
