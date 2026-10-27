package gal.udc.fic.prperez.pleste.service.dao.template;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@DisplayName("FieldTypes Enum Unit Tests")
public class FieldTypesTest {

	@Test
	@DisplayName("FieldTypes contains exactly 4 types")
	void testValuesCount() {
		assertEquals(4, FieldTypes.values().length);
	}

	@Test
	@DisplayName("TEXT constant has expected name")
	void testTextName() {
		assertEquals("TEXT", FieldTypes.TEXT.name());
	}

	@Test
	@DisplayName("DATETIME constant has expected name")
	void testDatetimeName() {
		assertEquals("DATETIME", FieldTypes.DATETIME.name());
	}

	@Test
	@DisplayName("LINK constant has expected name")
	void testLinkName() {
		assertEquals("LINK", FieldTypes.LINK.name());
	}

	@Test
	@DisplayName("NUMBER constant has expected name")
	void testNumberName() {
		assertEquals("NUMBER", FieldTypes.NUMBER.name());
	}

	@Test
	@DisplayName("valueOf resolves TEXT correctly")
	void testValueOfText() {
		assertEquals(FieldTypes.TEXT, FieldTypes.valueOf("TEXT"));
	}

	@Test
	@DisplayName("valueOf resolves DATETIME correctly")
	void testValueOfDatetime() {
		assertEquals(FieldTypes.DATETIME, FieldTypes.valueOf("DATETIME"));
	}

	@Test
	@DisplayName("valueOf resolves LINK correctly")
	void testValueOfLink() {
		assertEquals(FieldTypes.LINK, FieldTypes.valueOf("LINK"));
	}

	@Test
	@DisplayName("valueOf resolves NUMBER correctly")
	void testValueOfNumber() {
		assertEquals(FieldTypes.NUMBER, FieldTypes.valueOf("NUMBER"));
	}

	@Test
	@DisplayName("valueOf with unknown value throws IllegalArgumentException")
	void testValueOfUnknownThrows() {
		assertThrows(IllegalArgumentException.class, () -> FieldTypes.valueOf("UNKNOWN_TYPE"));
	}

	@Test
	@DisplayName("valueOf with null throws NullPointerException")
	void testValueOfNullThrows() {
		assertThrows(NullPointerException.class, () -> FieldTypes.valueOf(null));
	}
}
