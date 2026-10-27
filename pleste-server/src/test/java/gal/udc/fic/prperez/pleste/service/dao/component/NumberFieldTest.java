package gal.udc.fic.prperez.pleste.service.dao.component;

import gal.udc.fic.prperez.pleste.service.dao.template.FieldTypes;
import gal.udc.fic.prperez.pleste.service.dao.template.TemplateField;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("NumberField Unit Tests")
public class NumberFieldTest {

	@Test
	@DisplayName("Default constructor initializes null content")
	void testDefaultConstructor() {
		NumberField field = new NumberField();
		assertNull(field.getContent());
	}

	@Test
	@DisplayName("Full constructor sets content correctly")
	void testFullConstructorContent() {
		TemplateField tf = new TemplateField(1L, "voltage", true, FieldTypes.NUMBER);
		BigDecimal value = new BigDecimal("230.50");
		NumberField field = new NumberField(1L, value, tf);
		assertEquals(value, field.getContent());
	}

	@Test
	@DisplayName("Full constructor sets templateField correctly")
	void testFullConstructorTemplateField() {
		TemplateField tf = new TemplateField(1L, "voltage", true, FieldTypes.NUMBER);
		NumberField field = new NumberField(1L, BigDecimal.ONE, tf);
		assertEquals(tf, field.getTemplateField());
	}

	@Test
	@DisplayName("Full constructor sets type to NUMBER")
	void testFullConstructorType() {
		TemplateField tf = new TemplateField(1L, "voltage", true, FieldTypes.NUMBER);
		NumberField field = new NumberField(1L, BigDecimal.ONE, tf);
		assertEquals(FieldTypes.NUMBER, field.getType());
	}

	@Test
	@DisplayName("setId and getId work as expected")
	void testSetId() {
		NumberField field = new NumberField();
		field.setId(99L);
		assertEquals(99L, field.getId());
	}

	@Test
	@DisplayName("setContent with zero")
	void testSetContentZero() {
		NumberField field = new NumberField();
		field.setContent(BigDecimal.ZERO);
		assertEquals(BigDecimal.ZERO, field.getContent());
	}

	@Test
	@DisplayName("setContent with positive integer")
	void testSetContentPositive() {
		NumberField field = new NumberField();
		BigDecimal val = new BigDecimal("12345");
		field.setContent(val);
		assertEquals(val, field.getContent());
	}

	@Test
	@DisplayName("setContent with negative value")
	void testSetContentNegative() {
		NumberField field = new NumberField();
		BigDecimal val = new BigDecimal("-45.67");
		field.setContent(val);
		assertEquals(val, field.getContent());
	}

	@Test
	@DisplayName("setContent with high precision decimal")
	void testSetContentHighPrecision() {
		NumberField field = new NumberField();
		BigDecimal val = new BigDecimal("0.000000000123456789");
		field.setContent(val);
		assertEquals(val, field.getContent());
	}

	@Test
	@DisplayName("setContent with null")
	void testSetContentNull() {
		NumberField field = new NumberField();
		field.setContent(null);
		assertNull(field.getContent());
	}

	@Test
	@DisplayName("getFieldType returns NUMBER")
	void testGetFieldType() {
		NumberField field = new NumberField();
		assertEquals(FieldTypes.NUMBER, field.getFieldType());
	}

	@Test
	@DisplayName("setTemplateField updates reference")
	void testSetTemplateField() {
		NumberField field = new NumberField();
		TemplateField tf = new TemplateField(3L, "amperage", false, FieldTypes.NUMBER);
		field.setTemplateField(tf);
		assertEquals(tf, field.getTemplateField());
	}

	@Test
	@DisplayName("equals returns true for same id")
	void testEqualsSameId() {
		NumberField f1 = new NumberField(5L, BigDecimal.ONE, null);
		NumberField f2 = new NumberField(5L, BigDecimal.TEN, null);
		assertEquals(f1, f2);
	}

	@Test
	@DisplayName("equals returns false for different id")
	void testEqualsDifferentId() {
		NumberField f1 = new NumberField(5L, BigDecimal.ONE, null);
		NumberField f2 = new NumberField(6L, BigDecimal.ONE, null);
		assertNotEquals(f1, f2);
	}
}
