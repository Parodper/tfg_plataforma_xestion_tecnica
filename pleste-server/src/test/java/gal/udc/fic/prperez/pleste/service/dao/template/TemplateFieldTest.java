package gal.udc.fic.prperez.pleste.service.dao.template;

import net.jqwik.api.ForAll;
import net.jqwik.api.Property;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("TemplateField Entity Unit Tests")
public class TemplateFieldTest {

	@Test
	@DisplayName("Default constructor leaves id as null")
	void testDefaultConstructorIdNull() {
		TemplateField field = new TemplateField();
		assertNull(field.getId());
	}

	@Test
	@DisplayName("Default constructor leaves name as null")
	void testDefaultConstructorNameNull() {
		TemplateField field = new TemplateField();
		assertNull(field.getName());
	}

	@Test
	@DisplayName("Default constructor sets mandatory to false")
	void testDefaultConstructorMandatoryFalse() {
		TemplateField field = new TemplateField();
		assertFalse(field.isMandatory());
	}

	@Test
	@DisplayName("Default constructor leaves type as null")
	void testDefaultConstructorTypeNull() {
		TemplateField field = new TemplateField();
		assertNull(field.getType());
	}

	@Test
	@DisplayName("Id constructor sets id")
	void testIdConstructor() {
		TemplateField field = new TemplateField(42L);
		assertEquals(42L, field.getId());
	}

	@Test
	@DisplayName("Full constructor sets id")
	void testFullConstructorId() {
		TemplateField field = new TemplateField(1L, "serial", true, FieldTypes.TEXT);
		assertEquals(1L, field.getId());
	}

	@Test
	@DisplayName("Full constructor sets name")
	void testFullConstructorName() {
		TemplateField field = new TemplateField(1L, "serial", true, FieldTypes.TEXT);
		assertEquals("serial", field.getName());
	}

	@Test
	@DisplayName("Full constructor sets mandatory")
	void testFullConstructorMandatory() {
		TemplateField field = new TemplateField(1L, "serial", true, FieldTypes.TEXT);
		assertTrue(field.isMandatory());
	}

	@Test
	@DisplayName("Full constructor sets type")
	void testFullConstructorType() {
		TemplateField field = new TemplateField(1L, "serial", true, FieldTypes.TEXT);
		assertEquals(FieldTypes.TEXT, field.getType());
	}

	@Test
	@DisplayName("Boundary value: zero id")
	void testBoundaryZeroId() {
		TemplateField field = new TemplateField(0L, "test", false, FieldTypes.NUMBER);
		assertEquals(0L, field.getId());
	}

	@Test
	@DisplayName("Boundary value: max long id")
	void testBoundaryMaxLongId() {
		TemplateField field = new TemplateField(Long.MAX_VALUE, "test", false, FieldTypes.NUMBER);
		assertEquals(Long.MAX_VALUE, field.getId());
	}

	@Test
	@DisplayName("Boundary value: empty string name")
	void testBoundaryEmptyName() {
		TemplateField field = new TemplateField(1L, "", false, FieldTypes.TEXT);
		assertEquals("", field.getName());
	}

	@Test
	@DisplayName("Boundary value: null name")
	void testBoundaryNullName() {
		TemplateField field = new TemplateField(1L, null, false, FieldTypes.TEXT);
		assertNull(field.getName());
	}

	@Test
	@DisplayName("setId updates field id")
	void testSetId() {
		TemplateField field = new TemplateField();
		field.setId(99L);
		assertEquals(99L, field.getId());
	}

	@Test
	@DisplayName("setName updates field name")
	void testSetName() {
		TemplateField field = new TemplateField();
		field.setName("newField");
		assertEquals("newField", field.getName());
	}

	@Test
	@DisplayName("setMandatory updates mandatory flag")
	void testSetMandatory() {
		TemplateField field = new TemplateField();
		field.setMandatory(true);
		assertTrue(field.isMandatory());
	}

	@Test
	@DisplayName("setType updates field type")
	void testSetType() {
		TemplateField field = new TemplateField();
		field.setType(FieldTypes.DATETIME);
		assertEquals(FieldTypes.DATETIME, field.getType());
	}

	@Test
	@DisplayName("equals returns true for same instance")
	void testEqualsSameInstance() {
		TemplateField field = new TemplateField(10L);
		assertEquals(field, field);
	}

	@Test
	@DisplayName("equals returns true for distinct instances with same id")
	void testEqualsSameId() {
		TemplateField field1 = new TemplateField(10L, "A", true, FieldTypes.TEXT);
		TemplateField field2 = new TemplateField(10L, "B", false, FieldTypes.LINK);
		assertEquals(field1, field2);
	}

	@Test
	@DisplayName("equals returns false for distinct ids")
	void testEqualsDifferentId() {
		TemplateField field1 = new TemplateField(10L);
		TemplateField field2 = new TemplateField(20L);
		assertNotEquals(field1, field2);
	}

	@Test
	@DisplayName("equals returns false when comparing against null")
	void testEqualsNull() {
		TemplateField field = new TemplateField(10L);
		assertNotEquals(null, field);
	}

	@Test
	@DisplayName("equals returns false when comparing against different class")
	void testEqualsDifferentClass() {
		TemplateField field = new TemplateField(10L);
		assertNotEquals("10", field);
	}

	@Test
	@DisplayName("equals returns true when both ids are null")
	void testEqualsBothIdsNull() {
		TemplateField field1 = new TemplateField();
		TemplateField field2 = new TemplateField();
		assertEquals(field1, field2);
	}

	@Test
	@DisplayName("equals returns false when one id is null and other is not")
	void testEqualsOneIdNull() {
		TemplateField field1 = new TemplateField(1L);
		TemplateField field2 = new TemplateField();
		assertNotEquals(field1, field2);
	}

	@Test
	@DisplayName("hashCode matches for identical ids")
	void testHashCodeSameId() {
		TemplateField field1 = new TemplateField(50L);
		TemplateField field2 = new TemplateField(50L);
		assertEquals(field1.hashCode(), field2.hashCode());
	}

	@Test
	@DisplayName("hashCode is 0 when id is null")
	void testHashCodeNullId() {
		TemplateField field = new TemplateField();
		assertEquals(0, field.hashCode());
	}

	@Property
	@DisplayName("jqwik: constructor consistently preserves random id")
	void propertyConstructorPreservesId(@ForAll Long id, @ForAll String name, @ForAll boolean mandatory) {
		TemplateField field = new TemplateField(id, name, mandatory, FieldTypes.TEXT);
		assertEquals(id, field.getId());
	}

	@Property
	@DisplayName("jqwik: constructor consistently preserves random name")
	void propertyConstructorPreservesName(@ForAll Long id, @ForAll String name, @ForAll boolean mandatory) {
		TemplateField field = new TemplateField(id, name, mandatory, FieldTypes.TEXT);
		assertEquals(name, field.getName());
	}

	@Property
	@DisplayName("jqwik: two instances with identical random id are always equal")
	void propertyEqualityWithSameId(@ForAll Long id) {
		TemplateField f1 = new TemplateField(id);
		TemplateField f2 = new TemplateField(id);
		assertEquals(f1, f2);
	}
}
