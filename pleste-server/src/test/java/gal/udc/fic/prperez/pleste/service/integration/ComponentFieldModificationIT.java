package gal.udc.fic.prperez.pleste.service.integration;

import gal.udc.fic.prperez.pleste.service.Application;
import gal.udc.fic.prperez.pleste.service.ComponentResource;
import gal.udc.fic.prperez.pleste.service.JSONString;
import gal.udc.fic.prperez.pleste.service.TemplateResource;
import gal.udc.fic.prperez.pleste.service.dao.component.Component;
import gal.udc.fic.prperez.pleste.service.dao.component.Field;
import gal.udc.fic.prperez.pleste.service.dao.component.LinkField;
import gal.udc.fic.prperez.pleste.service.dao.component.NumberField;
import gal.udc.fic.prperez.pleste.service.dao.component.TextField;
import gal.udc.fic.prperez.pleste.service.dao.template.FieldTypes;
import gal.udc.fic.prperez.pleste.service.dao.template.Template;
import gal.udc.fic.prperez.pleste.service.dao.template.TemplateField;
import gal.udc.fic.prperez.pleste.service.exceptions.component.ComponentFieldInvalidValueException;
import gal.udc.fic.prperez.pleste.service.exceptions.component.ComponentFieldIsMandatoryException;
import gal.udc.fic.prperez.pleste.service.exceptions.component.ComponentFieldNotFoundException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(classes = Application.class)
@DisplayName("Integration Tests: Component Field Modification and Validation")
public class ComponentFieldModificationIT {

	@Autowired
	private ComponentResource componentResource;

	@Autowired
	private TemplateResource templateResource;

	private Long templateId;
	private Template template;
	private Long targetTemplateId;
	private Long componentId;
	private Long targetComponentId;

	@BeforeEach
	void setUp() {
		Template t = new Template();
		t.setName("FieldsTemplate_" + System.nanoTime());
		t.setDescription("Template with various field types");

		List<TemplateField> fields = new ArrayList<>();
		fields.add(new TemplateField(null, "mandatoryText", true, FieldTypes.TEXT));
		fields.add(new TemplateField(null, "optionalNotes", false, FieldTypes.TEXT));
		fields.add(new TemplateField(null, "calibrationDate", false, FieldTypes.DATETIME));
		fields.add(new TemplateField(null, "maxPressure", false, FieldTypes.NUMBER));
		fields.add(new TemplateField(null, "linkedModule", false, FieldTypes.LINK));
		t.setFields(fields);

		templateId = templateResource.addTemplate(t);
		template = templateResource.getTemplate(templateId.toString());

		// Create main component
		Component comp = new Component();
		comp.setName("Device_Alpha");
		comp.setTemplate(template);
		List<Field<?>> compFields = new ArrayList<>();
		compFields.add(new TextField(null, "Initial mandatory text", template.getFields().get(0)));
		comp.setFields(compFields);
		componentId = componentResource.addComponent(comp);

		// Create secondary target template and component for LINK testing
		Template targetT = new Template();
		targetT.setName("TargetTemplate_" + System.nanoTime());
		targetT.setDescription("Target template for link testing");
		List<TemplateField> targetFieldsList = new ArrayList<>();
		targetFieldsList.add(new TemplateField(null, "uniqueTargetField", true, FieldTypes.TEXT));
		targetT.setFields(targetFieldsList);
		targetTemplateId = templateResource.addTemplate(targetT);
		Template targetTemplate = templateResource.getTemplate(targetTemplateId.toString());

		Component target = new Component();
		target.setName("Target_Module");
		target.setTemplate(targetTemplate);
		List<Field<?>> targetFields = new ArrayList<>();
		targetFields.add(new TextField(null, "Target mandatory text", targetTemplate.getFields().get(0)));
		target.setFields(targetFields);
		targetComponentId = componentResource.addComponent(target);
	}

	@AfterEach
	void tearDown() {
		try {
			if (componentId != null)
				componentResource.removeComponent(componentId.toString());
		} catch (Exception ignored) {
		}
		try {
			if (targetComponentId != null)
				componentResource.removeComponent(targetComponentId.toString());
		} catch (Exception ignored) {
		}
		try {
			if (templateId != null)
				templateResource.removeTemplate(templateId.toString());
		} catch (Exception ignored) {
		}
		try {
			if (targetTemplateId != null)
				templateResource.removeTemplate(targetTemplateId.toString());
		} catch (Exception ignored) {
		}
	}

	@Test
	@DisplayName("Modify TEXT field content persists new value")
	void testModifyTextFieldContentSuccess() {
		componentResource.modifyFieldComponent(
				componentId.toString(), "optionalNotes", new JSONString("Updated operational notes"));

		Field field = componentResource.getFieldComponent(componentId.toString(), "optionalNotes");
		assertEquals("Updated operational notes", field.getContent());
	}

	@Disabled("ISSUE #2: Falla con NonUniqueResultException por consulta global getByTemplateFieldName sin filtrar por componentId")
	@Test
	@DisplayName("Modify field fails with NonUniqueResultException when multiple components share field name")
	void testModifyFieldWithMultipleComponentsThrowsNonUniqueResult() {
		Component secondComp = new Component();
		secondComp.setName("Device_Beta");
		secondComp.setTemplate(template);
		List<Field<?>> compFields = new ArrayList<>();
		compFields.add(new TextField(null, "Second component text", template.getFields().get(0)));
		secondComp.setFields(compFields);
		Long secondCompId = componentResource.addComponent(secondComp);

		try {
			componentResource.modifyFieldComponent(
					componentId.toString(), "optionalNotes", new JSONString("Updated notes for Alpha"));
		} finally {
			try {
				componentResource.removeComponent(secondCompId.toString());
			} catch (Exception ignored) {
			}
		}
	}

	@Test
	@DisplayName("Modify mandatory field with null value throws ComponentFieldIsMandatoryException")
	void testModifyMandatoryFieldWithNullThrows() {
		assertThrows(ComponentFieldIsMandatoryException.class,
				() -> componentResource.modifyFieldComponent(componentId.toString(), "mandatoryText", null));
	}

	@Test
	@DisplayName("Modify DATETIME field with valid ISO string updates content")
	void testModifyDatetimeFieldValid() {
		componentResource.modifyFieldComponent(
				componentId.toString(), "calibrationDate", new JSONString("2026-10-04T15:30:00Z"));

		Field field = componentResource.getFieldComponent(componentId.toString(), "calibrationDate");
		assertNotNull(field.getContent());
	}

	@Test
	@DisplayName("Modify DATETIME field with invalid format throws ComponentFieldInvalidValueException")
	void testModifyDatetimeFieldInvalidFormatThrows() {
		assertThrows(ComponentFieldInvalidValueException.class,
				() -> componentResource.modifyFieldComponent(
						componentId.toString(), "calibrationDate", new JSONString("not-a-valid-date")));
	}

	@Test
	@DisplayName("Modify NUMBER field with valid decimal updates value")
	void testModifyNumberFieldValid() {
		componentResource.modifyFieldComponent(
				componentId.toString(), "maxPressure", new JSONString("150.75"));

		NumberField field = (NumberField) componentResource.getFieldComponent(componentId.toString(), "maxPressure");
		assertEquals(new BigDecimal("150.75"), field.getContent());
	}

	@Test
	@DisplayName("Modify LINK field with valid target component establishes link")
	void testModifyLinkFieldValidTarget() {
		componentResource.modifyFieldComponent(
				componentId.toString(), "linkedModule", new JSONString(targetComponentId.toString()));

		LinkField field = (LinkField) componentResource.getFieldComponent(componentId.toString(), "linkedModule");
		assertEquals(targetComponentId, field.getContent().getId());
	}

	@Test
	@DisplayName("Remove optional field sets content to null")
	void testRemoveOptionalFieldSetsNull() {
		componentResource.modifyFieldComponent(
				componentId.toString(), "optionalNotes", new JSONString("Some note"));
		componentResource.removeComponentField(componentId.toString(), "optionalNotes");

		Field field = componentResource.getFieldComponent(componentId.toString(), "optionalNotes");
		assertNull(field.getContent());
	}

	@Test
	@DisplayName("Remove mandatory field throws ComponentFieldIsMandatoryException")
	void testRemoveMandatoryFieldThrows() {
		assertThrows(ComponentFieldIsMandatoryException.class,
				() -> componentResource.removeComponentField(componentId.toString(), "mandatoryText"));
	}

	@Test
	@DisplayName("Get non-existent field throws ComponentFieldNotFoundException")
	void testGetNonExistentFieldThrows() {
		assertThrows(ComponentFieldNotFoundException.class,
				() -> componentResource.getFieldComponent(componentId.toString(), "nonExistentField"));
	}
}
