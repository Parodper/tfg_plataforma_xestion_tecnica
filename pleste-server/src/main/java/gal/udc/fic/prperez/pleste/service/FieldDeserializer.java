package gal.udc.fic.prperez.pleste.service;

import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.core.JsonLocation;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import gal.udc.fic.prperez.pleste.service.dao.component.*;
import gal.udc.fic.prperez.pleste.service.dao.template.FieldTypes;
import gal.udc.fic.prperez.pleste.service.dao.template.TemplateField;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.Objects;

public class FieldDeserializer extends StdDeserializer<Field> {
	public static class FieldDeserializerJacksonException extends JacksonException {
		private final JsonLocation location;
		private final String originalMessage;
		private final FieldDeserializer processor;

		public FieldDeserializerJacksonException(JsonLocation location, String originalMessage, FieldDeserializer processor, Throwable cause) {
			super(originalMessage, cause);
			this.location = location;
			this.originalMessage = originalMessage;
			this.processor = processor;
		}

		@Override
		public JsonLocation getLocation() {
			return location;
		}

		@Override
		public String getOriginalMessage() {
			return originalMessage;
		}

		@Override
		public Object getProcessor() {
			return processor;
		}
	}

	protected FieldDeserializer(Class<?> vc) {
		super(vc);
	}

	public FieldDeserializer() {
		this(null);
	}

	@Override
	public Field deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException, JacksonException {
		JsonNode node = jsonParser.getCodec().readTree(jsonParser);
		Field<?> field;

		Long id;
		TemplateField templateField;

		try {
			id = node.get("id").asLong();
		} catch (NullPointerException e) {
			id = null;
		}

		templateField = jsonParser.getCodec().readValue(node.get("templateField").traverse(), TemplateField.class);

		try {
			FieldTypes type = FieldTypes.valueOf(node.get("type").asText());
			try {
				field = switch (type) {
					case TEXT -> new TextField(id, node.get("content").asText(), templateField);
					case DATETIME -> new DatetimeField(id,
							jsonParser.getCodec().readValue(node.get("content").traverse(), JSONDatetime.class),
							templateField);
					case LINK -> new LinkField(id,
							new Component(node.get("content").get("id").asLong()),
							templateField);
					case NUMBER ->
							new NumberField(id, BigDecimal.valueOf(node.get("content").asDouble()), templateField);
				};
			} catch (IllegalArgumentException e) {
				throw new FieldDeserializerJacksonException(jsonParser.currentLocation(), "Wrong content: " + node.get("type").asText(), this, e);
			} catch (NullPointerException e) {
				field = switch (type) {
					case TEXT -> new TextField(id, "", templateField);
					case DATETIME -> new DatetimeField(id, null, templateField);
					case LINK -> new LinkField(id, null, templateField);
					case NUMBER -> new NumberField(id, null, templateField);
				};
			}
		} catch (IllegalArgumentException e) {
			throw new FieldDeserializerJacksonException(jsonParser.currentLocation(), "Invalid field type: " + node.get("type").asText(), this, e);
		}

		return field;
	}
}
