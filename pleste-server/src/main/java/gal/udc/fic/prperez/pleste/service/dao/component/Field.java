package gal.udc.fic.prperez.pleste.service.dao.component;

import gal.udc.fic.prperez.pleste.service.dao.template.TemplateField;
import jakarta.persistence.*;

import java.util.Objects;

@Entity
public class Field {
	@Id
	@GeneratedValue
	private Long id;
	private String name;
	private String content;
	private Long link;
	@ManyToOne(optional = false)
	private TemplateField templateField;

	public Field(Long id, String name, String content, TemplateField templateField, Long link) {
		this.id = id;
		this.name = name;
		this.content = content;
		this.templateField = templateField;
		this.link = link;
	}

	public Field(Long id) {
		this.id = id;
	}

	public Field() {}

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

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public TemplateField getTemplateField() {
		return templateField;
	}

	public void setTemplateField(TemplateField templateField) {
		this.templateField = templateField;
	}

	public Long getLink() {
		return link;
	}

	public void setLink(Long link) {
		this.link = link;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (!(o instanceof Field field)) return false;
		return Objects.equals(id, field.id);
	}

	@Override
	public int hashCode() {
		return Objects.hash(id);
	}
}
