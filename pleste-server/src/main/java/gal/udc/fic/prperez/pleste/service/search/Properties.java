package gal.udc.fic.prperez.pleste.service.search;

public enum Properties {
	TEMPLATE_NAME("template.name"),
	TEMPLATE_DESCRIPTION("template.description"),
	TEMPLATE_FIELD_NAME("template.field.name"),
	TEMPLATE_FIELD_TYPE("template.field.type"),

	COMPONENT_NAME("component.name"),
	COMPONENT_DESCRIPTION("component.description"),
	COMPONENT_FIELD_NAME("component.field.name"),
	COMPONENT_FIELD_TYPE("component.field.type"),
	COMPONENT_FIELD_VALUE("component.field.value"),
	COMPONENT_FIELD_LINK("component.field.link"),
	COMPONENT_FIELD_DATE("component.field.date"),

	USER_NAME("user.name"),
	USER_EMAIL("user.email"),
	USER_ROLE("user.role");

	private final String property;

	Properties(String s) {
		this.property = s;
	}

	public String getProperty() {
		return property;
	}

	public static Properties getProperty(String s) throws IllegalArgumentException {
		for (Properties p : Properties.values()) {
			if (s.equals(p.getProperty())) {
				return p;
			}
		}
		throw new IllegalArgumentException();
	}
}
