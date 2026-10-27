package gal.udc.fic.prperez.pleste.service.dao.component;

import gal.udc.fic.prperez.pleste.service.dao.template.FieldTypes;
import gal.udc.fic.prperez.pleste.service.dao.template.TemplateField;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Field Base Class Unit Tests")
public class FieldTest {

	private static class ConcreteField extends Field<String> {
		public ConcreteField() {
			super();
		}

		public ConcreteField(Long id) {
			super(id);
		}

		public ConcreteField(Long id, String content, TemplateField templateField) {
			super(id, content, templateField);
		}

		@Override
		public String getContent() {
			return content;
		}

		@Override
		public void setContent(String content) {
			this.content = content;
		}

		@Override
		protected FieldTypes getFieldType() {
			return FieldTypes.TEXT;
		}
	}

	@Test
	@DisplayName("Field empty constructor sets id to null")
	void testDefaultConstructor() {
		ConcreteField field = new ConcreteField();
		assertNull(field.getId());
	}

	@Test
	@DisplayName("Field id constructor sets id")
	void testIdConstructor() {
		ConcreteField field = new ConcreteField(55L);
		assertEquals(55L, field.getId());
	}

	@Test
	@DisplayName("Field full constructor sets id")
	void testFullConstructorId() {
		TemplateField tf = new TemplateField(1L, "title", true, FieldTypes.TEXT);
		ConcreteField field = new ConcreteField(100L, "value", tf);
		assertEquals(100L, field.getId());
	}

	@Test
	@DisplayName("Field full constructor sets templateField")
	void testFullConstructorTemplateField() {
		TemplateField tf = new TemplateField(1L, "title", true, FieldTypes.TEXT);
		ConcreteField field = new ConcreteField(100L, "value", tf);
		assertEquals(tf, field.getTemplateField());
	}

	@Test
	@DisplayName("Field full constructor sets type from getFieldType")
	void testFullConstructorType() {
		TemplateField tf = new TemplateField(1L, "title", true, FieldTypes.TEXT);
		ConcreteField field = new ConcreteField(100L, "value", tf);
		assertEquals(FieldTypes.TEXT, field.getType());
	}

	@Test
	@DisplayName("setId and getId work as expected")
	void testSetId() {
		ConcreteField field = new ConcreteField();
		field.setId(77L);
		assertEquals(77L, field.getId());
	}

	@Test
	@DisplayName("setTemplateField and getTemplateField work as expected")
	void testSetTemplateField() {
		ConcreteField field = new ConcreteField();
		TemplateField tf = new TemplateField(2L, "description", false, FieldTypes.TEXT);
		field.setTemplateField(tf);
		assertEquals(tf, field.getTemplateField());
	}

	@Test
	@DisplayName("equals returns true for same instance")
	void testEqualsSameInstance() {
		ConcreteField field = new ConcreteField(1L);
		assertEquals(field, field);
	}

	@Test
	@DisplayName("equals returns true for fields with same id")
	void testEqualsSameId() {
		ConcreteField f1 = new ConcreteField(10L);
		ConcreteField f2 = new ConcreteField(10L);
		assertEquals(f1, f2);
	}

	@Test
	@DisplayName("equals returns false for fields with different id")
	void testEqualsDifferentId() {
		ConcreteField f1 = new ConcreteField(10L);
		ConcreteField f2 = new ConcreteField(20L);
		assertNotEquals(f1, f2);
	}

	@Test
	@DisplayName("equals returns false when compared to null")
	void testEqualsNull() {
		ConcreteField f1 = new ConcreteField(10L);
		assertNotEquals(null, f1);
	}

	@Test
	@DisplayName("equals returns false when compared to different type")
	void testEqualsDifferentType() {
		ConcreteField f1 = new ConcreteField(10L);
		assertNotEquals("StringObject", f1);
	}

	@Test
	@DisplayName("equals returns true when both have null id")
	void testEqualsBothNullId() {
		ConcreteField f1 = new ConcreteField();
		ConcreteField f2 = new ConcreteField();
		assertEquals(f1, f2);
	}

	@Test
	@DisplayName("hashCode returns same value for same id")
	void testHashCodeSameId() {
		ConcreteField f1 = new ConcreteField(42L);
		ConcreteField f2 = new ConcreteField(42L);
		assertEquals(f1.hashCode(), f2.hashCode());
	}

	@Test
	@DisplayName("hashCode returns consistent value for null id")
	void testHashCodeNullId() {
		ConcreteField f1 = new ConcreteField();
		ConcreteField f2 = new ConcreteField();
		assertEquals(f1.hashCode(), f2.hashCode());
	}
}
