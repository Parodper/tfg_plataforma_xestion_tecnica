package gal.udc.fic.prperez.pleste.service.search;

import gal.udc.fic.prperez.pleste.service.dao.component.*;
import gal.udc.fic.prperez.pleste.service.dao.template.Template;
import gal.udc.fic.prperez.pleste.service.dao.users.User;
import gal.udc.fic.prperez.pleste.service.exceptions.ParseSearchException;

public class TestString extends Node {
	private final Properties property;
	private final String value;
	private final boolean isEqual;

	public TestString(String property, String value, boolean isEqual) {
		super();
		this.property = Properties.getProperty(property);
		this.value = value;
		this.isEqual = isEqual;
	}
	
	@Override
	public String toString() {
		return property + " LIKE " + value;
	}

	@Override
	public boolean matches(Object n) {
		boolean result = false;
		
		if(n instanceof Template t) {
			result = switch (this.property) {
				case TEMPLATE_NAME -> t.getName().matches(this.value);
				case TEMPLATE_DESCRIPTION -> t.getDescription().matches(this.value);
				case TEMPLATE_FIELD_NAME -> t.getFields().stream().anyMatch(f -> f.getName().matches(this.value));
				case TEMPLATE_FIELD_TYPE -> t.getFields().stream().anyMatch(f -> f.getType().name().matches(this.value));
				default -> throw new ParseSearchException("Tried to use " + this.property + " with a template");
			};
		} else if (n instanceof Component c) {
			result = switch (this.property) {
				case COMPONENT_NAME -> c.getName().matches(this.value);
				case COMPONENT_DESCRIPTION -> c.getDescription().matches(this.value);
				case COMPONENT_FIELD_NAME -> c.getFields().stream().anyMatch(f -> f.getTemplateField().getName().matches(this.value));
				case COMPONENT_FIELD_TYPE -> c.getFields().stream().anyMatch(f -> f.getTemplateField().getType().name().matches(this.value));
				case COMPONENT_FIELD_VALUE -> c.getFields().stream().anyMatch(f -> switch (f.getTemplateField().getType()) {
					case TEXT -> ((TextField) f).getContent().matches(this.value);
					case DATETIME -> ((DatetimeField) f).getContent().toString().matches(this.value);
					default -> false;
				});
				default -> throw new ParseSearchException("Tried to use " + this.property + " with a component");
			};
		} else if (n instanceof User u) {
			result = switch (this.property) {
				case USER_NAME -> u.getUsername().matches(this.value);
				case USER_EMAIL -> u.getEmail().matches(this.value);
				case USER_ROLE -> u.getRole().name().matches(this.value);
				default -> throw new ParseSearchException("Tried to use " + this.property + " with a user");
			};
		}
		
		return result == isEqual;
	}
}
