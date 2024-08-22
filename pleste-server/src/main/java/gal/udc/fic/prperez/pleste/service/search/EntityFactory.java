package gal.udc.fic.prperez.pleste.service.search;

import gal.udc.fic.prperez.pleste.service.dao.component.Component;
import gal.udc.fic.prperez.pleste.service.dao.component.Field;
import gal.udc.fic.prperez.pleste.service.dao.template.Template;
import gal.udc.fic.prperez.pleste.service.dao.template.TemplateField;
import gal.udc.fic.prperez.pleste.service.dao.users.User;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;

public class EntityFactory {
	private CriteriaQuery<?> query;

	private Root<Template> templateRoot;
	private Root<TemplateField> templateFieldRoot;
	private Root<Component> componentRoot;
	private Root<Field> fieldRoot;
	private Root<User> userRoot;

	public EntityFactory() {
		this.templateRoot = null;
		this.templateFieldRoot = null;
		this.componentRoot = null;
		this.fieldRoot = null;
		this.userRoot = null;
		this.query = null;
	}

	public EntityFactory(CriteriaQuery<?> query) {
		this();
		this.query = query;
	}

	public Root<Template> getTemplateRoot() {
		if (templateRoot == null) {
			templateRoot = query.from(Template.class);
		}
		return templateRoot;
	}

	public Root<TemplateField> getTemplateFieldRoot() {
		if (templateFieldRoot == null) {
			templateFieldRoot = query.from(TemplateField.class);
		}
		return templateFieldRoot;
	}

	public Root<Component> getComponentRoot() {
		if (componentRoot == null) {
			componentRoot = query.from(Component.class);
		}
		return componentRoot;
	}

	public Root<Field> getFieldRoot() {
		if (fieldRoot == null) {
			fieldRoot = query.from(Field.class);
		}
		return fieldRoot;
	}

	public Root<User> getUserRoot() {
		if (userRoot == null) {
			userRoot = query.from(User.class);
		}
		return userRoot;
	}
}
