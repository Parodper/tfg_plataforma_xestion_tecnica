package gal.udc.fic.prperez.pleste.service.dao.component;

import gal.udc.fic.prperez.pleste.service.dao.template.TemplateField;
import jakarta.persistence.*;

@Entity
public class Field {
	@Id
	@GeneratedValue
	private Long id;
	private String content;
	@OneToOne(optional = false)
	private TemplateField templateField;
	@ManyToOne
	private Component link;

	public Field(Long id, String content, TemplateField templateField, Component link) {
		this.id = id;
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

	public Component getLink() {
		return link;
	}

	public void setLink(Component link) {
		this.link = link;
	}
}
