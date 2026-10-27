package gal.udc.fic.prperez.pleste.service.dao.component;

import gal.udc.fic.prperez.pleste.service.dao.template.FieldTypes;
import gal.udc.fic.prperez.pleste.service.dao.template.TemplateField;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("TextField Unit Tests")
public class TextFieldTest {

	@Test
	@DisplayName("Default constructor initializes null fields")
	void testDefaultConstructor() {
		TextField field = new TextField();
		assertNull(field.getContent());
	}

	@Test
	@DisplayName("Full constructor sets content correctly")
	void testFullConstructorContent() {
		TemplateField tf = new TemplateField(1L, "title", true, FieldTypes.TEXT);
		TextField field = new TextField(10L, "Sample text", tf);
		assertEquals("Sample text", field.getContent());
	}

	@Test
	@DisplayName("Full constructor sets templateField correctly")
	void testFullConstructorTemplateField() {
		TemplateField tf = new TemplateField(1L, "title", true, FieldTypes.TEXT);
		TextField field = new TextField(10L, "Sample text", tf);
		assertEquals(tf, field.getTemplateField());
	}

	@Test
	@DisplayName("Full constructor sets type to TEXT")
	void testFullConstructorType() {
		TemplateField tf = new TemplateField(1L, "title", true, FieldTypes.TEXT);
		TextField field = new TextField(10L, "Sample text", tf);
		assertEquals(FieldTypes.TEXT, field.getType());
	}

	@Test
	@DisplayName("setId and getId work as expected")
	void testSetId() {
		TextField field = new TextField();
		field.setId(25L);
		assertEquals(25L, field.getId());
	}

	@Test
	@DisplayName("setContent with normal string")
	void testSetContentNormal() {
		TextField field = new TextField();
		field.setContent("Normal content string");
		assertEquals("Normal content string", field.getContent());
	}

	@Test
	@DisplayName("setContent with empty string")
	void testSetContentEmpty() {
		TextField field = new TextField();
		field.setContent("");
		assertEquals("", field.getContent());
	}

	@Test
	@DisplayName("setContent with null string")
	void testSetContentNull() {
		TextField field = new TextField();
		field.setContent(null);
		assertNull(field.getContent());
	}

	@Test
	@DisplayName("setContent with special characters")
	void testSetContentSpecialCharacters() {
		TextField field = new TextField();
		field.setContent("!@#$%^&*()_+-=[]{}|;':,.<>?/`~ñáéíóú");
		assertEquals("!@#$%^&*()_+-=[]{}|;':,.<>?/`~ñáéíóú", field.getContent());
	}

	@Test
	@DisplayName("getFieldType returns TEXT")
	void testGetFieldType() {
		TextField field = new TextField();
		assertEquals(FieldTypes.TEXT, field.getFieldType());
	}

	@Test
	@DisplayName("setTemplateField updates template field reference")
	void testSetTemplateField() {
		TextField field = new TextField();
		TemplateField tf = new TemplateField(2L, "summary", false, FieldTypes.TEXT);
		field.setTemplateField(tf);
		assertEquals(tf, field.getTemplateField());
	}

	@Test
	@DisplayName("equals returns true for same id")
	void testEqualsSameId() {
		TextField f1 = new TextField(1L, "a", null);
		TextField f2 = new TextField(1L, "b", null);
		assertEquals(f1, f2);
	}

	@Test
	@DisplayName("equals returns false for different id")
	void testEqualsDifferentId() {
		TextField f1 = new TextField(1L, "a", null);
		TextField f2 = new TextField(2L, "a", null);
		assertNotEquals(f1, f2);
	}
}
