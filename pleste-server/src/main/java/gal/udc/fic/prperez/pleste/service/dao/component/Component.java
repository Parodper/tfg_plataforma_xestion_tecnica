package gal.udc.fic.prperez.pleste.service.dao.component;

import gal.udc.fic.prperez.pleste.service.dao.template.Template;
import jakarta.persistence.*;

import java.util.List;

@Entity
@Table(name = "components")
public class Component {
	@Id
	@GeneratedValue
	private Long id;
	@OneToOne
	private Template template;
	@OneToMany( fetch = FetchType.EAGER, cascade = CascadeType.ALL )
	private List<Field> fields;

	public Component(Long id, Template template, List<Field> fields) {
		this.id = id;
		this.template = template;
		this.fields = fields;
	}

	public Component() {}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Template getTemplate() {
		return template;
	}

	public void setTemplate(Template template) {
		this.template = template;
	}

	public List<Field> getFields() {
		return fields;
	}

	public void setFields(List<Field> fields) {
		this.fields = fields;
	}
}