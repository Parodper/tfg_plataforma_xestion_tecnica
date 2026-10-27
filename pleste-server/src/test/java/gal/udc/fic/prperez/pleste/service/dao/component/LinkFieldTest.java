package gal.udc.fic.prperez.pleste.service.dao.component;

import gal.udc.fic.prperez.pleste.service.dao.template.FieldTypes;
import gal.udc.fic.prperez.pleste.service.dao.template.TemplateField;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("LinkField Unit Tests")
public class LinkFieldTest {

	@Test
	@DisplayName("Default constructor initializes null content")
	void testDefaultConstructor() {
		LinkField field = new LinkField();
		assertNull(field.getContent());
	}

	@Test
	@DisplayName("Full constructor sets content correctly")
	void testFullConstructorContent() {
		TemplateField tf = new TemplateField(1L, "parentDevice", true, FieldTypes.LINK);
		Component linked = new Component(50L);
		LinkField field = new LinkField(1L, linked, tf);
		assertEquals(linked, field.getContent());
	}

	@Test
	@DisplayName("Full constructor sets templateField correctly")
	void testFullConstructorTemplateField() {
		TemplateField tf = new TemplateField(1L, "parentDevice", true, FieldTypes.LINK);
		Component linked = new Component(50L);
		LinkField field = new LinkField(1L, linked, tf);
		assertEquals(tf, field.getTemplateField());
	}

	@Test
	@DisplayName("Full constructor sets type to LINK")
	void testFullConstructorType() {
		TemplateField tf = new TemplateField(1L, "parentDevice", true, FieldTypes.LINK);
		Component linked = new Component(50L);
		LinkField field = new LinkField(1L, linked, tf);
		assertEquals(FieldTypes.LINK, field.getType());
	}

	@Test
	@DisplayName("setId and getId work as expected")
	void testSetId() {
		LinkField field = new LinkField();
		field.setId(88L);
		assertEquals(88L, field.getId());
	}

	@Test
	@DisplayName("setContent with valid component")
	void testSetContentValid() {
		LinkField field = new LinkField();
		Component linked = new Component(100L);
		field.setContent(linked);
		assertEquals(linked, field.getContent());
	}

	@Test
	@DisplayName("setContent with null")
	void testSetContentNull() {
		LinkField field = new LinkField();
		field.setContent(null);
		assertNull(field.getContent());
	}

	@Test
	@DisplayName("getFieldType returns LINK")
	void testGetFieldType() {
		LinkField field = new LinkField();
		assertEquals(FieldTypes.LINK, field.getFieldType());
	}

	@Test
	@DisplayName("setTemplateField updates reference")
	void testSetTemplateField() {
		LinkField field = new LinkField();
		TemplateField tf = new TemplateField(5L, "related", false, FieldTypes.LINK);
		field.setTemplateField(tf);
		assertEquals(tf, field.getTemplateField());
	}

	@Test
	@DisplayName("equals returns true for same id")
	void testEqualsSameId() {
		LinkField f1 = new LinkField(30L, null, null);
		LinkField f2 = new LinkField(30L, null, null);
		assertEquals(f1, f2);
	}

	@Test
	@DisplayName("equals returns false for different id")
	void testEqualsDifferentId() {
		LinkField f1 = new LinkField(30L, null, null);
		LinkField f2 = new LinkField(31L, null, null);
		assertNotEquals(f1, f2);
	}
}
