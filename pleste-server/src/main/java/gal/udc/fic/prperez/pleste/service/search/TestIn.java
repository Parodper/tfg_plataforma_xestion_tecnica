package gal.udc.fic.prperez.pleste.service.search;

import gal.udc.fic.prperez.pleste.service.dao.component.*;
import gal.udc.fic.prperez.pleste.service.dao.template.Template;
import gal.udc.fic.prperez.pleste.service.dao.users.User;
import gal.udc.fic.prperez.pleste.service.exceptions.ParseSearchException;

import java.math.BigDecimal;
import java.util.List;

public class TestIn extends Node {
	private final Properties property;
	private final List<String> values;
	private final boolean isEqual;

	public TestIn(String property, List<String> values, boolean isEqual) {
		super();
		this.property = Properties.getProperty(property);
		this.values = values;
		this.isEqual = isEqual;
	}

	@Override
	public String toString() {
		return property + " IN ( " + values + " )";
	}

	@Override
	public boolean matches(Object n) {
		boolean result = false;

		if(n instanceof Template t) {
			result = switch (this.property) {
				case TEMPLATE_NAME -> values.stream().anyMatch(t.getName()::matches);

				case TEMPLATE_DESCRIPTION -> values.stream().anyMatch(t.getDescription()::matches);

				case TEMPLATE_FIELD_NAME -> t.getFields().stream().anyMatch(f -> values.stream().anyMatch(f.getName()::matches));

				case TEMPLATE_FIELD_TYPE -> t.getFields().stream().anyMatch(f -> values.stream().anyMatch(f.getType().name()::matches));

				default -> throw new ParseSearchException("Tried to use " + this.property + " with a template");
			};
		} else if (n instanceof Component c) {
			result = switch (this.property) {
				case COMPONENT_NAME -> values.stream().anyMatch(c.getName()::matches);

				case COMPONENT_DESCRIPTION -> values.stream().anyMatch(c.getDescription()::matches);

				case COMPONENT_FIELD_NAME ->
						c.getFields().stream().anyMatch(f -> values.stream().anyMatch(f.getTemplateField().getName()::matches));

				case COMPONENT_FIELD_TYPE ->
						c.getFields().stream().anyMatch(f -> values.stream().anyMatch(f.getTemplateField().getType().name()::matches));

				case COMPONENT_FIELD_VALUE -> c.getFields().stream().anyMatch(f -> {
					try {
						return switch (f.getTemplateField().getType()) {
							case TEXT -> values.stream().anyMatch(((TextField) f).getContent()::matches);

							case DATETIME ->
									values.stream().anyMatch(((DatetimeField) f).getContent().toString()::matches);

							case LINK ->
									values.stream().map(Long::parseLong).anyMatch(((LinkField) f).getContent().getId()::equals);

							case NUMBER ->
									values.stream().map(BigDecimal::new).anyMatch(((NumberField) f).getContent()::equals);
						};
					} catch (NumberFormatException ignored) {
						return false;
					}
				});

				default -> throw new ParseSearchException("Tried to use " + this.property + " with a component");
			};
		} else if (n instanceof User u) {
			result = switch (this.property) {
				case USER_NAME -> values.stream().anyMatch(u.getUsername()::matches);

				case USER_EMAIL -> values.stream().anyMatch(u.getEmail()::matches);

				case USER_ROLE -> values.stream().anyMatch(u.getRole().name()::matches);

				default -> throw new ParseSearchException("Tried to use " + this.property + " with a user");
			};
		}

		return result == isEqual;
	}
}
