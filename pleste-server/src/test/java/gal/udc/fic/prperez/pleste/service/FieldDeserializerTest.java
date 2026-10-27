package gal.udc.fic.prperez.pleste.service;

import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.databind.ObjectMapper;
import gal.udc.fic.prperez.pleste.service.dao.component.*;
import gal.udc.fic.prperez.pleste.service.dao.template.FieldTypes;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("FieldDeserializer Unit Tests")
public class FieldDeserializerTest {

	private ObjectMapper objectMapper;

	@BeforeEach
	void setUp() {
		objectMapper = new ObjectMapper();
		objectMapper.findAndRegisterModules();
	}

	@Test
	@DisplayName("Deserialize TEXT field with valid content creates TextField")
	void testDeserializeText() throws Exception {
		String json = """
				{
				  "id": 1,
				  "type": "TEXT",
				  "content": "Sample value",
				  "templateField": { "id": 10, "name": "title", "mandatory": true, "type": "TEXT" }
				}
				""";
		Field<?> field = objectMapper.readValue(json, Field.class);
		assertInstanceOf(TextField.class, field);
	}

	@Test
	@DisplayName("Deserialize TEXT field with valid content preserves content value")
	void testDeserializeTextContent() throws Exception {
		String json = """
				{
				  "id": 1,
				  "type": "TEXT",
				  "content": "Sample value",
				  "templateField": { "id": 10, "name": "title", "mandatory": true, "type": "TEXT" }
				}
				""";
		Field<?> field = objectMapper.readValue(json, Field.class);
		assertEquals("Sample value", field.getContent());
	}

	@Test
	@DisplayName("Deserialize TEXT field with missing content defaults to empty string")
	void testDeserializeTextMissingContent() throws Exception {
		String json = """
				{
				  "id": 1,
				  "type": "TEXT",
				  "templateField": { "id": 10, "name": "title", "mandatory": false, "type": "TEXT" }
				}
				""";
		Field<?> field = objectMapper.readValue(json, Field.class);
		assertEquals("", field.getContent());
	}

	@Test
	@DisplayName("Deserialize NUMBER field creates NumberField instance")
	void testDeserializeNumberType() throws Exception {
		String json = """
				{
				  "id": 2,
				  "type": "NUMBER",
				  "content": 42.5,
				  "templateField": { "id": 11, "name": "price", "mandatory": true, "type": "NUMBER" }
				}
				""";
		Field<?> field = objectMapper.readValue(json, Field.class);
		assertInstanceOf(NumberField.class, field);
	}

	@Test
	@DisplayName("Deserialize NUMBER field preserves BigDecimal value")
	void testDeserializeNumberContent() throws Exception {
		String json = """
				{
				  "id": 2,
				  "type": "NUMBER",
				  "content": 42.5,
				  "templateField": { "id": 11, "name": "price", "mandatory": true, "type": "NUMBER" }
				}
				""";
		Field<?> field = objectMapper.readValue(json, Field.class);
		assertEquals(BigDecimal.valueOf(42.5), field.getContent());
	}

	@Test
	@DisplayName("Deserialize NUMBER field with missing content sets content to null")
	void testDeserializeNumberMissingContent() throws Exception {
		String json = """
				{
				  "id": 2,
				  "type": "NUMBER",
				  "templateField": { "id": 11, "name": "price", "mandatory": false, "type": "NUMBER" }
				}
				""";
		Field<?> field = objectMapper.readValue(json, Field.class);
		assertNull(field.getContent());
	}

	@Test
	@DisplayName("Deserialize DATETIME field creates DatetimeField instance")
	void testDeserializeDatetimeType() throws Exception {
		String json = """
				{
				  "id": 3,
				  "type": "DATETIME",
				  "content": { "datetime": "2026-10-04T12:00:00Z" },
				  "templateField": { "id": 12, "name": "timestamp", "mandatory": true, "type": "DATETIME" }
				}
				""";
		Field<?> field = objectMapper.readValue(json, Field.class);
		assertInstanceOf(DatetimeField.class, field);
	}

	@Test
	@DisplayName("Deserialize DATETIME field with missing content sets content to null")
	void testDeserializeDatetimeMissingContent() throws Exception {
		String json = """
				{
				  "id": 3,
				  "type": "DATETIME",
				  "templateField": { "id": 12, "name": "timestamp", "mandatory": false, "type": "DATETIME" }
				}
				""";
		Field<?> field = objectMapper.readValue(json, Field.class);
		assertNull(field.getContent());
	}

	@Test
	@DisplayName("Deserialize LINK field creates LinkField instance")
	void testDeserializeLinkType() throws Exception {
		String json = """
				{
				  "id": 4,
				  "type": "LINK",
				  "content": { "id": 99 },
				  "templateField": { "id": 13, "name": "parent", "mandatory": true, "type": "LINK" }
				}
				""";
		Field<?> field = objectMapper.readValue(json, Field.class);
		assertInstanceOf(LinkField.class, field);
	}

	@Test
	@DisplayName("Deserialize LINK field links to Component with specified id")
	void testDeserializeLinkId() throws Exception {
		String json = """
				{
				  "id": 4,
				  "type": "LINK",
				  "content": { "id": 99 },
				  "templateField": { "id": 13, "name": "parent", "mandatory": true, "type": "LINK" }
				}
				""";
		LinkField field = (LinkField) objectMapper.readValue(json, Field.class);
		assertEquals(99L, field.getContent().getId());
	}

	@Test
	@DisplayName("Deserialize LINK field with missing content sets content to null")
	void testDeserializeLinkMissingContent() throws Exception {
		String json = """
				{
				  "id": 4,
				  "type": "LINK",
				  "templateField": { "id": 13, "name": "parent", "mandatory": false, "type": "LINK" }
				}
				""";
		Field<?> field = objectMapper.readValue(json, Field.class);
		assertNull(field.getContent());
	}

	@Test
	@DisplayName("Deserialize field with invalid type throws exception")
	void testDeserializeInvalidType() {
		String json = """
				{
				  "id": 5,
				  "type": "UNKNOWN_TYPE",
				  "templateField": { "id": 14, "name": "test", "mandatory": false, "type": "TEXT" }
				}
				""";
		assertThrows(JacksonException.class, () -> objectMapper.readValue(json, Field.class));
	}

	@Test
	@DisplayName("Deserialize field without id sets id to null")
	void testDeserializeMissingId() throws Exception {
		String json = """
				{
				  "type": "TEXT",
				  "content": "No ID here",
				  "templateField": { "id": 15, "name": "note", "mandatory": false, "type": "TEXT" }
				}
				""";
		Field<?> field = objectMapper.readValue(json, Field.class);
		assertNull(field.getId());
	}

	@Test
	@DisplayName("FieldDeserializerJacksonException getters return expected values")
	void testCustomExceptionGetters() {
		FieldDeserializer deserializer = new FieldDeserializer();
		FieldDeserializer.FieldDeserializerJacksonException ex =
				new FieldDeserializer.FieldDeserializerJacksonException(null, "Test error message", deserializer, null);
		assertEquals("Test error message", ex.getOriginalMessage());
	}
}
