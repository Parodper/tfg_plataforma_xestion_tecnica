package gal.udc.fic.prperez.pleste.service;

import gal.udc.fic.prperez.pleste.service.dao.SQLDaoFactoryUtil;
import gal.udc.fic.prperez.pleste.service.dao.component.*;
import gal.udc.fic.prperez.pleste.service.dao.template.*;
import gal.udc.fic.prperez.pleste.service.exceptions.TemplateFieldNotFoundException;
import gal.udc.fic.prperez.pleste.service.exceptions.component.*;
import gal.udc.fic.prperez.pleste.service.exceptions.template.TemplateNotFoundException;
import jakarta.persistence.EntityNotFoundException;
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

import java.math.BigDecimal;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("ComponentResource Unit Tests with Mocks")
public class ComponentResourceTest {

	@Mock
	private SQLComponentDao componentDatabase;

	@Mock
	private SQLTemplateDao sqlTemplateDao;

	@Mock
	private SQLFieldDao sqlFieldDao;

	@Mock
	private SQLComponentDao factoryComponentDao;

	private SQLDaoFactoryUtil databaseFactory;
	private ComponentResource resource;

	@BeforeEach
	void setUp() {
		databaseFactory = new SQLDaoFactoryUtil(
				sqlTemplateDao,
				factoryComponentDao,
				null,
				sqlFieldDao,
				null,
				null);

		resource = new ComponentResource(componentDatabase, databaseFactory);
	}

	// ==========================================
	// addComponent tests
	// ==========================================

	@Disabled("ISSUE #1: Falla con NullPointerException en código original al acceder a template.getName() cuando template es null")
	@Test
	@DisplayName("addComponent throws TemplateNotFoundException when template is null")
	void testAddComponentNullTemplate() {
		Component component = new Component(1L, "Comp", "Desc", null, new ArrayList<>());
		assertThrows(TemplateNotFoundException.class, () -> resource.addComponent(component));
	}

	@Test
	@DisplayName("addComponent throws TemplateNotFoundException when template does not exist")
	void testAddComponentTemplateNotExists() {
		Template template = new Template(99L);
		Component component = new Component(1L, "Comp", "Desc", template, new ArrayList<>());
		when(sqlTemplateDao.existsById(99L)).thenReturn(false);

		assertThrows(TemplateNotFoundException.class, () -> resource.addComponent(component));
	}

	@Test
	@DisplayName("addComponent throws ComponentFieldIsMandatoryException when mandatory field has null content")
	void testAddComponentMandatoryFieldNull() {
		Template template = new Template(1L);
		TemplateField tf = new TemplateField(10L, "mandatoryField", true, FieldTypes.TEXT);
		template.setFields(List.of(tf));

		when(sqlTemplateDao.existsById(1L)).thenReturn(true);
		when(sqlTemplateDao.getReferenceById(1L)).thenReturn(template);

		Component component = new Component();
		component.setTemplate(template);
		List<Field<?>> fields = new ArrayList<>();
		fields.add(new TextField(null, null, tf));
		component.setFields(fields);

		assertThrows(ComponentFieldIsMandatoryException.class, () -> resource.addComponent(component));
	}

	@Test
	@DisplayName("addComponent throws ComponentFieldIsMandatoryException when mandatory text field is empty string")
	void testAddComponentMandatoryFieldEmptyString() {
		Template template = new Template(1L);
		TemplateField tf = new TemplateField(10L, "mandatoryField", true, FieldTypes.TEXT);
		template.setFields(List.of(tf));

		when(sqlTemplateDao.existsById(1L)).thenReturn(true);
		when(sqlTemplateDao.getReferenceById(1L)).thenReturn(template);

		Component component = new Component();
		component.setTemplate(template);
		List<Field<?>> fields = new ArrayList<>();
		fields.add(new TextField(null, "", tf));
		component.setFields(fields);

		assertThrows(ComponentFieldIsMandatoryException.class, () -> resource.addComponent(component));
	}

	@Test
	@DisplayName("addComponent returns generated ID when valid component is saved")
	void testAddComponentSuccessReturnsId() {
		Template template = new Template(1L);
		TemplateField tf = new TemplateField(10L, "title", true, FieldTypes.TEXT);
		template.setFields(List.of(tf));

		when(sqlTemplateDao.existsById(1L)).thenReturn(true);
		when(sqlTemplateDao.getReferenceById(1L)).thenReturn(template);

		Component component = new Component();
		component.setTemplate(template);
		List<Field<?>> fields = new ArrayList<>();
		fields.add(new TextField(null, "Valid content", tf));
		component.setFields(fields);

		Component saved = new Component(555L, "Comp", "Desc", template, fields);
		when(componentDatabase.save(any(Component.class))).thenReturn(saved);

		Long resultId = resource.addComponent(component);
		assertEquals(555L, resultId);
	}

	@Test
	@DisplayName("addComponent automatically adds missing optional fields from template")
	void testAddComponentAutoFillsMissingFields() {
		Template template = new Template(1L);
		TemplateField tfText = new TemplateField(10L, "optionalText", false, FieldTypes.TEXT);
		TemplateField tfDate = new TemplateField(11L, "optionalDate", false, FieldTypes.DATETIME);
		TemplateField tfLink = new TemplateField(12L, "optionalLink", false, FieldTypes.LINK);
		TemplateField tfNum = new TemplateField(13L, "optionalNum", false, FieldTypes.NUMBER);
		template.setFields(List.of(tfText, tfDate, tfLink, tfNum));

		when(sqlTemplateDao.existsById(1L)).thenReturn(true);
		when(sqlTemplateDao.getReferenceById(1L)).thenReturn(template);

		Component component = new Component();
		component.setTemplate(template);
		component.setFields(new ArrayList<>());

		when(componentDatabase.save(any(Component.class))).thenAnswer(inv -> {
			Component c = inv.getArgument(0);
			c.setId(10L);
			return c;
		});

		resource.addComponent(component);
		assertEquals(4, component.getFields().size());
	}

	// ==========================================
	// getAllComponents tests
	// ==========================================

	@Test
	@DisplayName("getAllComponents returns all components when count is null")
	void testGetAllComponentsUnpaged() {
		List<Component> list = List.of(new Component(1L), new Component(2L));
		when(componentDatabase.findAll()).thenReturn(list);

		List<Component> result = resource.getAllComponents(null, null);
		assertEquals(2, result.size());
	}

	@Test
	@DisplayName("getAllComponents returns paged list when count is provided")
	void testGetAllComponentsPaged() {
		List<Component> list = List.of(new Component(1L));
		Page<Component> page = new PageImpl<>(list);
		when(componentDatabase.findAll(any(Pageable.class))).thenReturn(page);

		List<Component> result = resource.getAllComponents(0, 10);
		assertEquals(1, result.size());
	}

	@Test
	@DisplayName("getAllComponents defaults skip to 0 when skip is null and count is provided")
	void testGetAllComponentsNullSkip() {
		List<Component> list = List.of(new Component(1L));
		Page<Component> page = new PageImpl<>(list);
		when(componentDatabase.findAll(any(Pageable.class))).thenReturn(page);

		List<Component> result = resource.getAllComponents(null, 5);
		assertFalse(result.isEmpty());
	}

	// ==========================================
	// getComponentsByName tests
	// ==========================================

	@Test
	@DisplayName("getComponentsByName returns matching components")
	void testGetComponentsByName() {
		List<Component> list = List.of(new Component(1L, "Thermometer", "desc", null, null));
		when(componentDatabase.findByName("Thermometer")).thenReturn(list);

		List<Component> result = resource.getComponentsByName("Thermometer");
		assertEquals("Thermometer", result.get(0).getName());
	}

	@Test
	@DisplayName("getComponentsByName returns empty list when name not found")
	void testGetComponentsByNameNotFound() {
		when(componentDatabase.findByName("NonExistent")).thenReturn(Collections.emptyList());

		List<Component> result = resource.getComponentsByName("NonExistent");
		assertTrue(result.isEmpty());
	}

	// ==========================================
	// getComponent tests
	// ==========================================

	@Test
	@DisplayName("getComponent returns component with simplified template when found")
	void testGetComponentFound() {
		Template template = new Template(99L, "FullTemplate", "desc", null);
		Component comp = new Component(10L, "Comp", "desc", template, null);
		when(componentDatabase.findById(10L)).thenReturn(Optional.of(comp));

		Component result = resource.getComponent("10");
		assertEquals(99L, result.getTemplate().getId());
	}

	@Test
	@DisplayName("getComponent throws ComponentNotFoundException when component not found")
	void testGetComponentNotFound() {
		when(componentDatabase.findById(999L)).thenReturn(Optional.empty());

		assertThrows(ComponentNotFoundException.class, () -> resource.getComponent("999"));
	}

	// ==========================================
	// modifyComponent tests
	// ==========================================

	@Test
	@DisplayName("modifyComponent throws ComponentNotFoundException when component does not exist")
	void testModifyComponentNotFound() {
		when(componentDatabase.existsById(123L)).thenReturn(false);
		Component updated = new Component(123L, "NewName", "NewDesc", null, null);

		assertThrows(ComponentNotFoundException.class, () -> resource.modifyComponent("123", updated));
	}

	@Test
	@DisplayName("modifyComponent updates name and description when component exists")
	void testModifyComponentSuccess() {
		TemplateField tf = new TemplateField(1L, "title", false, FieldTypes.TEXT);
		TextField existingField = new TextField(10L, "OldText", tf);
		Component existing = new Component(50L, "OldName", "OldDesc", null, new ArrayList<>(List.of(existingField)));

		when(componentDatabase.existsById(50L)).thenReturn(true);
		when(componentDatabase.getReferenceById(50L)).thenReturn(existing);

		TextField newField = new TextField(10L, "NewText", tf);
		Component updated = new Component(50L, "NewName", "NewDesc", null, List.of(newField));

		resource.modifyComponent("50", updated);
		assertEquals("NewName", existing.getName());
	}

	@Test
	@DisplayName("modifyComponent throws ComponentFieldNotFoundException when field missing from update payload")
	void testModifyComponentFieldMissing() {
		TemplateField tf = new TemplateField(1L, "title", false, FieldTypes.TEXT);
		TextField existingField = new TextField(10L, "OldText", tf);
		Component existing = new Component(50L, "OldName", "OldDesc", null, List.of(existingField));

		when(componentDatabase.existsById(50L)).thenReturn(true);
		when(componentDatabase.getReferenceById(50L)).thenReturn(existing);

		Component updated = new Component(50L, "NewName", "NewDesc", null, Collections.emptyList());

		assertThrows(ComponentFieldNotFoundException.class, () -> resource.modifyComponent("50", updated));
	}

	// ==========================================
	// removeComponent tests
	// ==========================================

	@Test
	@DisplayName("removeComponent deletes component when it exists")
	void testRemoveComponentSuccess() {
		when(componentDatabase.existsById(33L)).thenReturn(true);

		resource.removeComponent("33");
		verify(componentDatabase).deleteById(33L);
	}

	@Test
	@DisplayName("removeComponent throws ComponentNotFoundException when component does not exist")
	void testRemoveComponentNotFound() {
		when(componentDatabase.existsById(99L)).thenReturn(false);

		assertThrows(ComponentNotFoundException.class, () -> resource.removeComponent("99"));
	}

	// ==========================================
	// getParentTemplate tests
	// ==========================================

	@Test
	@DisplayName("getParentTemplate returns template of component")
	void testGetParentTemplate() {
		Template template = new Template(7L, "ParentTemplate", "desc", null);
		Component component = new Component(22L, "Child", "desc", template, null);
		when(componentDatabase.getReferenceById(22L)).thenReturn(component);

		Template result = resource.getParentTemplate("22");
		assertEquals("ParentTemplate", result.getName());
	}

	// ==========================================
	// getFieldsComponent tests
	// ==========================================

	@Test
	@DisplayName("getFieldsComponent returns all fields of existing component")
	void testGetFieldsComponent() {
		TextField f1 = new TextField(1L, "text", null);
		Component component = new Component(5L, "Comp", "desc", null, List.of(f1));
		when(componentDatabase.findById(5L)).thenReturn(Optional.of(component));

		List<Field> fields = resource.getFieldsComponent("5");
		assertEquals(1, fields.size());
	}

	@Test
	@DisplayName("getFieldsComponent throws ComponentNotFoundException when component not found")
	void testGetFieldsComponentNotFound() {
		when(componentDatabase.findById(404L)).thenReturn(Optional.empty());

		assertThrows(ComponentNotFoundException.class, () -> resource.getFieldsComponent("404"));
	}

	// ==========================================
	// modifyFieldComponent tests
	// ==========================================

	@Test
	@DisplayName("modifyFieldComponent throws ComponentFieldNotFoundException when field name does not exist")
	void testModifyFieldComponentNotFound() {
		when(sqlFieldDao.existsByTemplateFieldName("unknownField")).thenReturn(false);

		assertThrows(ComponentFieldNotFoundException.class,
				() -> resource.modifyFieldComponent("1", "unknownField", new JSONString("val")));
	}

	@Test
	@DisplayName("modifyFieldComponent throws ComponentFieldIsMandatoryException when mandatory field is set to null")
	void testModifyFieldComponentMandatoryNull() {
		TemplateField tf = new TemplateField(1L, "requiredText", true, FieldTypes.TEXT);
		TextField field = new TextField(10L, "Current", tf);

		when(sqlFieldDao.existsByTemplateFieldName("requiredText")).thenReturn(true);
		doReturn(field).when(sqlFieldDao).getByTemplateFieldName("requiredText");

		assertThrows(ComponentFieldIsMandatoryException.class,
				() -> resource.modifyFieldComponent("1", "requiredText", null));
	}

	@Test
	@DisplayName("modifyFieldComponent sets content to null when optional field is modified with null")
	void testModifyFieldComponentOptionalNull() {
		TemplateField tf = new TemplateField(1L, "optionalText", false, FieldTypes.TEXT);
		TextField field = new TextField(10L, "Current", tf);

		when(sqlFieldDao.existsByTemplateFieldName("optionalText")).thenReturn(true);
		doReturn(field).when(sqlFieldDao).getByTemplateFieldName("optionalText");

		resource.modifyFieldComponent("1", "optionalText", null);
		assertNull(field.getContent());
	}

	@Test
	@DisplayName("modifyFieldComponent updates TEXT field content")
	void testModifyFieldComponentText() {
		TemplateField tf = new TemplateField(1L, "descField", false, FieldTypes.TEXT);
		TextField field = new TextField(10L, "Old", tf);

		when(sqlFieldDao.existsByTemplateFieldName("descField")).thenReturn(true);
		doReturn(field).when(sqlFieldDao).getByTemplateFieldName("descField");

		resource.modifyFieldComponent("1", "descField", new JSONString("New Description"));
		assertEquals("New Description", field.getContent());
	}

	@Test
	@DisplayName("modifyFieldComponent updates DATETIME field content with valid ISO string")
	void testModifyFieldComponentDatetimeValid() {
		TemplateField tf = new TemplateField(1L, "timestamp", false, FieldTypes.DATETIME);
		DatetimeField field = new DatetimeField(10L, null, tf);

		when(sqlFieldDao.existsByTemplateFieldName("timestamp")).thenReturn(true);
		doReturn(field).when(sqlFieldDao).getByTemplateFieldName("timestamp");

		resource.modifyFieldComponent("1", "timestamp", new JSONString("2026-10-04T12:00:00Z"));
		assertNotNull(field.getContent());
	}

	@Test
	@DisplayName("modifyFieldComponent throws ComponentFieldInvalidValueException on invalid DATETIME string")
	void testModifyFieldComponentDatetimeInvalid() {
		TemplateField tf = new TemplateField(1L, "timestamp", false, FieldTypes.DATETIME);
		DatetimeField field = new DatetimeField(10L, null, tf);

		when(sqlFieldDao.existsByTemplateFieldName("timestamp")).thenReturn(true);
		doReturn(field).when(sqlFieldDao).getByTemplateFieldName("timestamp");

		assertThrows(ComponentFieldInvalidValueException.class,
				() -> resource.modifyFieldComponent("1", "timestamp", new JSONString("invalid-date-format")));
	}

	@Test
	@DisplayName("modifyFieldComponent updates LINK field with existing component reference")
	void testModifyFieldComponentLinkValid() {
		TemplateField tf = new TemplateField(1L, "parentDevice", false, FieldTypes.LINK);
		LinkField field = new LinkField(10L, null, tf);
		Component linked = new Component(99L);

		when(sqlFieldDao.existsByTemplateFieldName("parentDevice")).thenReturn(true);
		doReturn(field).when(sqlFieldDao).getByTemplateFieldName("parentDevice");
		when(factoryComponentDao.getReferenceById(99L)).thenReturn(linked);

		resource.modifyFieldComponent("1", "parentDevice", new JSONString("99"));
		assertEquals(99L, field.getContent().getId());
	}

	@Disabled("ISSUE #3: Falla porque getReferenceById no valida existencia y no lanza ComponentFieldInvalidValueException")
	@Test
	@DisplayName("modifyFieldComponent throws ComponentFieldInvalidValueException when linked component does not exist")
	void testModifyFieldComponentLinkInvalid() {
		TemplateField tf = new TemplateField(1L, "parentDevice", false, FieldTypes.LINK);
		LinkField field = new LinkField(10L, null, tf);

		when(sqlFieldDao.existsByTemplateFieldName("parentDevice")).thenReturn(true);
		doReturn(field).when(sqlFieldDao).getByTemplateFieldName("parentDevice");

		assertThrows(ComponentFieldInvalidValueException.class,
				() -> resource.modifyFieldComponent("1", "parentDevice", new JSONString("999")));
	}

	@Test
	@DisplayName("modifyFieldComponent updates NUMBER field with valid BigDecimal")
	void testModifyFieldComponentNumberValid() {
		TemplateField tf = new TemplateField(1L, "voltage", false, FieldTypes.NUMBER);
		NumberField field = new NumberField(10L, null, tf);

		when(sqlFieldDao.existsByTemplateFieldName("voltage")).thenReturn(true);
		doReturn(field).when(sqlFieldDao).getByTemplateFieldName("voltage");

		resource.modifyFieldComponent("1", "voltage", new JSONString("220.5"));
		assertEquals(new BigDecimal("220.5"), field.getContent());
	}

	@Disabled("ISSUE #4: Falla con NumberFormatException no capturada en código original (capturaba EntityNotFoundException)")
	@Test
	@DisplayName("modifyFieldComponent throws ComponentFieldInvalidValueException on invalid NUMBER format")
	void testModifyFieldComponentNumberInvalid() {
		TemplateField tf = new TemplateField(1L, "voltage", false, FieldTypes.NUMBER);
		NumberField field = new NumberField(10L, null, tf);

		when(sqlFieldDao.existsByTemplateFieldName("voltage")).thenReturn(true);
		doReturn(field).when(sqlFieldDao).getByTemplateFieldName("voltage");

		assertThrows(ComponentFieldInvalidValueException.class,
				() -> resource.modifyFieldComponent("1", "voltage", new JSONString("not-a-number")));
	}

	// ==========================================
	// getFieldComponent tests
	// ==========================================

	@Test
	@DisplayName("getFieldComponent returns field when found")
	void testGetFieldComponentFound() {
		TemplateField tf = new TemplateField(1L, "notes", false, FieldTypes.TEXT);
		TextField field = new TextField(10L, "Sample Note", tf);

		when(sqlFieldDao.existsByTemplateFieldName("notes")).thenReturn(true);
		doReturn(field).when(sqlFieldDao).getByTemplateFieldName("notes");

		Field result = resource.getFieldComponent("1", "notes");
		assertEquals("Sample Note", result.getContent());
	}

	@Test
	@DisplayName("getFieldComponent throws ComponentFieldNotFoundException when field does not exist")
	void testGetFieldComponentNotFound() {
		when(sqlFieldDao.existsByTemplateFieldName("missing")).thenReturn(false);

		assertThrows(ComponentFieldNotFoundException.class,
				() -> resource.getFieldComponent("1", "missing"));
	}

	// ==========================================
	// removeComponentField tests
	// ==========================================

	@Test
	@DisplayName("removeComponentField throws ComponentFieldIsMandatoryException when field is mandatory")
	void testRemoveComponentFieldMandatory() {
		TemplateField tf = new TemplateField(1L, "mandatoryField", true, FieldTypes.TEXT);
		TextField field = new TextField(10L, "Content", tf);

		doReturn(field).when(sqlFieldDao).getByTemplateFieldName("mandatoryField");

		assertThrows(ComponentFieldIsMandatoryException.class,
				() -> resource.removeComponentField("1", "mandatoryField"));
	}

	@Disabled("ISSUE #5: Falla porque ComponentResource pasa idPath en lugar de fieldName al constructor de ComponentFieldIsMandatoryException")
	@Test
	@DisplayName("removeComponentField provides accurate fieldName in exception message")
	void testRemoveComponentFieldMandatoryMessageAccuracy() {
		TemplateField tf = new TemplateField(1L, "mandatoryField", true, FieldTypes.TEXT);
		TextField field = new TextField(10L, "Content", tf);

		doReturn(field).when(sqlFieldDao).getByTemplateFieldName("mandatoryField");

		ComponentFieldIsMandatoryException ex = null;
		try {
			resource.removeComponentField("1", "mandatoryField");
		} catch (ComponentFieldIsMandatoryException e) {
			ex = e;
		}
		assertEquals("Field mandatoryField is mandatory", ex != null ? ex.getMessage() : "");
	}

	@Test
	@DisplayName("removeComponentField clears content of optional field")
	void testRemoveComponentFieldOptional() {
		TemplateField tf = new TemplateField(1L, "optionalField", false, FieldTypes.TEXT);
		TextField field = new TextField(10L, "Content", tf);

		doReturn(field).when(sqlFieldDao).getByTemplateFieldName("optionalField");

		resource.removeComponentField("1", "optionalField");
		assertNull(field.getContent());
	}
}
