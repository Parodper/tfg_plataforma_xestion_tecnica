package gal.udc.fic.prperez.pleste.service.dao.component;

import gal.udc.fic.prperez.pleste.service.dao.template.Template;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Component Unit Tests")
public class ComponentTest {

	@Test
	@DisplayName("Default constructor initializes an empty component")
	void testDefaultConstructor() {
		Component component = new Component();
		assertNull(component.getId());
	}

	@Test
	@DisplayName("Id-only constructor sets the id")
	void testIdConstructor() {
		Component component = new Component(10L);
		assertEquals(10L, component.getId());
	}

	@Test
	@DisplayName("Full constructor sets all attributes - check ID")
	void testFullConstructorId() {
		Template template = new Template(1L);
		List<Field<?>> fields = new ArrayList<>();
		Component component = new Component(100L, "Sensor", "Temperature sensor", template, fields);
		assertEquals(100L, component.getId());
	}

	@Test
	@DisplayName("Full constructor sets all attributes - check Name")
	void testFullConstructorName() {
		Template template = new Template(1L);
		List<Field<?>> fields = new ArrayList<>();
		Component component = new Component(100L, "Sensor", "Temperature sensor", template, fields);
		assertEquals("Sensor", component.getName());
	}

	@Test
	@DisplayName("Full constructor sets all attributes - check Description")
	void testFullConstructorDescription() {
		Template template = new Template(1L);
		List<Field<?>> fields = new ArrayList<>();
		Component component = new Component(100L, "Sensor", "Temperature sensor", template, fields);
		assertEquals("Temperature sensor", component.getDescription());
	}

	@Test
	@DisplayName("Full constructor sets all attributes - check Template")
	void testFullConstructorTemplate() {
		Template template = new Template(1L);
		List<Field<?>> fields = new ArrayList<>();
		Component component = new Component(100L, "Sensor", "Temperature sensor", template, fields);
		assertEquals(template, component.getTemplate());
	}

	@Test
	@DisplayName("Full constructor sets all attributes - check Fields")
	void testFullConstructorFields() {
		Template template = new Template(1L);
		List<Field<?>> fields = new ArrayList<>();
		Component component = new Component(100L, "Sensor", "Temperature sensor", template, fields);
		assertSame(fields, component.getFields());
	}

	@Test
	@DisplayName("setId updates the id with boundary value")
	void testSetIdBoundary() {
		Component component = new Component();
		component.setId(Long.MAX_VALUE);
		assertEquals(Long.MAX_VALUE, component.getId());
	}

	@Test
	@DisplayName("setName updates name with standard value")
	void testSetNameStandard() {
		Component component = new Component();
		component.setName("Pressure Gauge");
		assertEquals("Pressure Gauge", component.getName());
	}

	@Test
	@DisplayName("setName updates name with empty string")
	void testSetNameEmpty() {
		Component component = new Component();
		component.setName("");
		assertEquals("", component.getName());
	}

	@Test
	@DisplayName("setName updates name with null")
	void testSetNameNull() {
		Component component = new Component();
		component.setName(null);
		assertNull(component.getName());
	}

	@Test
	@DisplayName("setDescription updates description with standard value")
	void testSetDescriptionStandard() {
		Component component = new Component();
		component.setDescription("Measures atmospheric pressure");
		assertEquals("Measures atmospheric pressure", component.getDescription());
	}

	@Test
	@DisplayName("setDescription updates description with null")
	void testSetDescriptionNull() {
		Component component = new Component();
		component.setDescription(null);
		assertNull(component.getDescription());
	}

	@Test
	@DisplayName("setTemplate updates template reference")
	void testSetTemplate() {
		Component component = new Component();
		Template template = new Template(5L);
		component.setTemplate(template);
		assertEquals(template, component.getTemplate());
	}

	@Test
	@DisplayName("setFields updates fields list")
	void testSetFields() {
		Component component = new Component();
		List<Field<?>> fields = new ArrayList<>();
		component.setFields(fields);
		assertSame(fields, component.getFields());
	}

	@Test
	@DisplayName("equals returns true for same instance")
	void testEqualsSameInstance() {
		Component component = new Component(1L);
		assertEquals(component, component);
	}

	@Test
	@DisplayName("equals returns true for components with same id")
	void testEqualsSameId() {
		Component c1 = new Component(42L);
		Component c2 = new Component(42L);
		assertEquals(c1, c2);
	}

	@Test
	@DisplayName("equals returns false for components with different id")
	void testEqualsDifferentId() {
		Component c1 = new Component(42L);
		Component c2 = new Component(43L);
		assertNotEquals(c1, c2);
	}

	@Test
	@DisplayName("equals returns false when comparing against null")
	void testEqualsNull() {
		Component c1 = new Component(42L);
		assertNotEquals(null, c1);
	}

	@Test
	@DisplayName("equals returns false when comparing against different class")
	void testEqualsDifferentClass() {
		Component c1 = new Component(42L);
		assertNotEquals("42", c1);
	}

	@Test
	@DisplayName("equals returns true when both have null id")
	void testEqualsBothNullId() {
		Component c1 = new Component();
		Component c2 = new Component();
		assertEquals(c1, c2);
	}

	@Test
	@DisplayName("hashCode produces same value for components with same id")
	void testHashCodeSameId() {
		Component c1 = new Component(42L);
		Component c2 = new Component(42L);
		assertEquals(c1.hashCode(), c2.hashCode());
	}

	@Test
	@DisplayName("hashCode produces consistent value for null id")
	void testHashCodeNullId() {
		Component c1 = new Component();
		Component c2 = new Component();
		assertEquals(c1.hashCode(), c2.hashCode());
	}
}
