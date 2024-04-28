package gal.udc.fic.prperez.pleste.service.dao.template;

import jakarta.persistence.*;

import java.util.List;


@Entity
public class Template {
	@Id
	@GeneratedValue
	private Long id;
	private String name;
	private String description;
	@OneToMany(cascade=CascadeType.ALL, fetch = FetchType.EAGER)
	private List<TemplateField> fields;

	public Template(Long id, String name, String description, List<TemplateField> fields) {
		this.id = id;
		this.name = name;
		this.description = description;
		this.fields = fields;
	}

	public Template(Long id) {
		this.id = id;
	}

	public Template() {}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
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

	public List<TemplateField> getFields() {
		return fields;
	}

	public void setFields(List<TemplateField> fields) {
		this.fields = fields;
	}
}
