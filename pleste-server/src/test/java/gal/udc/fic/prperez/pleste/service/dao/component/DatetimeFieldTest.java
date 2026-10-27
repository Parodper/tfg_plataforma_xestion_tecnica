package gal.udc.fic.prperez.pleste.service.dao.component;

import gal.udc.fic.prperez.pleste.service.JSONDatetime;
import gal.udc.fic.prperez.pleste.service.dao.template.FieldTypes;
import gal.udc.fic.prperez.pleste.service.dao.template.TemplateField;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("DatetimeField Unit Tests")
public class DatetimeFieldTest {

	@Test
	@DisplayName("Default constructor initializes null content")
	void testDefaultConstructor() {
		DatetimeField field = new DatetimeField();
		assertNull(field.getContent());
	}

	@Test
	@DisplayName("Full constructor sets content correctly")
	void testFullConstructorContent() {
		TemplateField tf = new TemplateField(1L, "createdAt", true, FieldTypes.DATETIME);
		JSONDatetime dt = JSONDatetime.now();
		DatetimeField field = new DatetimeField(1L, dt, tf);
		assertEquals(dt, field.getContent());
	}

	@Test
	@DisplayName("Full constructor sets templateField correctly")
	void testFullConstructorTemplateField() {
		TemplateField tf = new TemplateField(1L, "createdAt", true, FieldTypes.DATETIME);
		DatetimeField field = new DatetimeField(1L, JSONDatetime.now(), tf);
		assertEquals(tf, field.getTemplateField());
	}

	@Test
	@DisplayName("Full constructor sets type to DATETIME")
	void testFullConstructorType() {
		TemplateField tf = new TemplateField(1L, "createdAt", true, FieldTypes.DATETIME);
		DatetimeField field = new DatetimeField(1L, JSONDatetime.now(), tf);
		assertEquals(FieldTypes.DATETIME, field.getType());
	}

	@Test
	@DisplayName("setId and getId work as expected")
	void testSetId() {
		DatetimeField field = new DatetimeField();
		field.setId(77L);
		assertEquals(77L, field.getId());
	}

	@Test
	@DisplayName("setContent with valid JSONDatetime")
	void testSetContentValid() {
		DatetimeField field = new DatetimeField();
		JSONDatetime dt = JSONDatetime.parse("2026-10-04T12:00:00Z");
		field.setContent(dt);
		assertEquals(dt, field.getContent());
	}

	@Test
	@DisplayName("setContent with null")
	void testSetContentNull() {
		DatetimeField field = new DatetimeField();
		field.setContent(null);
		assertNull(field.getContent());
	}

	@Test
	@DisplayName("getFieldType returns DATETIME")
	void testGetFieldType() {
		DatetimeField field = new DatetimeField();
		assertEquals(FieldTypes.DATETIME, field.getFieldType());
	}

	@Test
	@DisplayName("setTemplateField updates reference")
	void testSetTemplateField() {
		DatetimeField field = new DatetimeField();
		TemplateField tf = new TemplateField(4L, "updatedAt", false, FieldTypes.DATETIME);
		field.setTemplateField(tf);
		assertEquals(tf, field.getTemplateField());
	}

	@Test
	@DisplayName("equals returns true for same id")
	void testEqualsSameId() {
		DatetimeField f1 = new DatetimeField(10L, null, null);
		DatetimeField f2 = new DatetimeField(10L, null, null);
		assertEquals(f1, f2);
	}

	@Test
	@DisplayName("equals returns false for different id")
	void testEqualsDifferentId() {
		DatetimeField f1 = new DatetimeField(10L, null, null);
		DatetimeField f2 = new DatetimeField(20L, null, null);
		assertNotEquals(f1, f2);
	}
}
