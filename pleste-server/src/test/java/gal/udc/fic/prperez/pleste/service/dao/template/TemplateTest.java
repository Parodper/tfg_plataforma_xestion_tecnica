package gal.udc.fic.prperez.pleste.service.dao.template;

import net.jqwik.api.ForAll;
import net.jqwik.api.Property;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Template Entity Unit Tests")
public class TemplateTest {

	@Test
	@DisplayName("Default constructor leaves id as null")
	void testDefaultConstructorIdNull() {
		Template template = new Template();
		assertNull(template.getId());
	}

	@Test
	@DisplayName("Default constructor leaves name as null")
	void testDefaultConstructorNameNull() {
		Template template = new Template();
		assertNull(template.getName());
	}

	@Test
	@DisplayName("Default constructor leaves description as null")
	void testDefaultConstructorDescriptionNull() {
		Template template = new Template();
		assertNull(template.getDescription());
	}

	@Test
	@DisplayName("Default constructor leaves fields as null")
	void testDefaultConstructorFieldsNull() {
		Template template = new Template();
		assertNull(template.getFields());
	}

	@Test
	@DisplayName("Id constructor sets id")
	void testIdConstructor() {
		Template template = new Template(100L);
		assertEquals(100L, template.getId());
	}

	@Test
	@DisplayName("Full constructor sets id")
	void testFullConstructorId() {
		Template template = new Template(1L, "Server", "Desc", new ArrayList<>());
		assertEquals(1L, template.getId());
	}

	@Test
	@DisplayName("Full constructor sets name")
	void testFullConstructorName() {
		Template template = new Template(1L, "Server", "Desc", new ArrayList<>());
		assertEquals("Server", template.getName());
	}

	@Test
	@DisplayName("Full constructor sets description")
	void testFullConstructorDescription() {
		Template template = new Template(1L, "Server", "Desc", new ArrayList<>());
		assertEquals("Desc", template.getDescription());
	}

	@Test
	@DisplayName("Full constructor sets fields list")
	void testFullConstructorFields() {
		List<TemplateField> fields = new ArrayList<>();
		fields.add(new TemplateField(1L, "IP", true, FieldTypes.TEXT));
		Template template = new Template(1L, "Server", "Desc", fields);
		assertEquals(1, template.getFields().size());
	}

	@Test
	@DisplayName("Boundary value: zero id")
	void testBoundaryZeroId() {
		Template template = new Template(0L, "Zero", "Desc", null);
		assertEquals(0L, template.getId());
	}

	@Test
	@DisplayName("Boundary value: max long id")
	void testBoundaryMaxLongId() {
		Template template = new Template(Long.MAX_VALUE, "Max", "Desc", null);
		assertEquals(Long.MAX_VALUE, template.getId());
	}

	@Test
	@DisplayName("Boundary value: empty string name and description")
	void testBoundaryEmptyStrings() {
		Template template = new Template(1L, "", "", new ArrayList<>());
		assertEquals("", template.getName());
	}

	@Test
	@DisplayName("Boundary value: null fields allowed")
	void testBoundaryNullFields() {
		Template template = new Template(1L, "T", "D", null);
		assertNull(template.getFields());
	}

	@Test
	@DisplayName("setId updates template id")
	void testSetId() {
		Template template = new Template();
		template.setId(77L);
		assertEquals(77L, template.getId());
	}

	@Test
	@DisplayName("setName updates template name")
	void testSetName() {
		Template template = new Template();
		template.setName("Router");
		assertEquals("Router", template.getName());
	}

	@Test
	@DisplayName("setDescription updates template description")
	void testSetDescription() {
		Template template = new Template();
		template.setDescription("Cisco Router Template");
		assertEquals("Cisco Router Template", template.getDescription());
	}

	@Test
	@DisplayName("setFields updates template fields list")
	void testSetFields() {
		Template template = new Template();
		List<TemplateField> fields = List.of(new TemplateField(1L, "mac", true, FieldTypes.TEXT));
		template.setFields(fields);
		assertEquals(1, template.getFields().size());
	}

	@Test
	@DisplayName("equals returns true for same instance")
	void testEqualsSameInstance() {
		Template template = new Template(5L);
		assertEquals(template, template);
	}

	@Test
	@DisplayName("equals returns true for distinct instances with same id")
	void testEqualsSameId() {
		Template t1 = new Template(5L, "T1", "D1", null);
		Template t2 = new Template(5L, "T2", "D2", null);
		assertEquals(t1, t2);
	}

	@Test
	@DisplayName("equals returns false for distinct ids")
	void testEqualsDifferentId() {
		Template t1 = new Template(5L);
		Template t2 = new Template(6L);
		assertNotEquals(t1, t2);
	}

	@Test
	@DisplayName("equals returns false when comparing against null")
	void testEqualsNull() {
		Template template = new Template(5L);
		assertNotEquals(null, template);
	}

	@Test
	@DisplayName("equals returns false when comparing against different class")
	void testEqualsDifferentClass() {
		Template template = new Template(5L);
		assertNotEquals("5", template);
	}

	@Test
	@DisplayName("equals returns true when both ids are null")
	void testEqualsBothIdsNull() {
		Template t1 = new Template();
		Template t2 = new Template();
		assertEquals(t1, t2);
	}

	@Test
	@DisplayName("equals returns false when one id is null and other is not")
	void testEqualsOneIdNull() {
		Template t1 = new Template(1L);
		Template t2 = new Template();
		assertNotEquals(t1, t2);
	}

	@Test
	@DisplayName("hashCode matches for identical ids")
	void testHashCodeSameId() {
		Template t1 = new Template(88L);
		Template t2 = new Template(88L);
		assertEquals(t1.hashCode(), t2.hashCode());
	}

	@Test
	@DisplayName("hashCode is 0 when id is null")
	void testHashCodeNullId() {
		Template template = new Template();
		assertEquals(0, template.hashCode());
	}

	@Property
	@DisplayName("jqwik: constructor consistently preserves random id")
	void propertyConstructorPreservesId(@ForAll Long id, @ForAll String name, @ForAll String desc) {
		Template template = new Template(id, name, desc, new ArrayList<>());
		assertEquals(id, template.getId());
	}

	@Property
	@DisplayName("jqwik: constructor consistently preserves random name")
	void propertyConstructorPreservesName(@ForAll Long id, @ForAll String name, @ForAll String desc) {
		Template template = new Template(id, name, desc, new ArrayList<>());
		assertEquals(name, template.getName());
	}

	@Property
	@DisplayName("jqwik: two instances with identical random id are always equal")
	void propertyEqualityWithSameId(@ForAll Long id) {
		Template t1 = new Template(id);
		Template t2 = new Template(id);
		assertEquals(t1, t2);
	}
}
