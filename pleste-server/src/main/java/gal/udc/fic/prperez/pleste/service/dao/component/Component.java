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

	private String name;
	private String description;
	@ManyToOne( fetch = FetchType.LAZY, optional = false)
	private Template template;
	@OneToMany( fetch = FetchType.EAGER, cascade = { CascadeType.PERSIST, CascadeType.REMOVE } )
	private List<Field> fields;

	public Component(Long id, String name, String description, Template template, List<Field> fields) {
		this.id = id;
		this.name = name;
		this.description = description;
		this.template = template;
		this.fields = fields;
	}

	public Component(Long id) {
		this.id = id;
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

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}
}
