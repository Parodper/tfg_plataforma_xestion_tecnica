package gal.udc.fic.prperez.pleste.service.dao.component;

import gal.udc.fic.prperez.pleste.service.dao.template.TemplateField;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;

@Entity
@DiscriminatorValue("TEXT")
public class TextField extends Field<String> {
	@Id
	private Long id;

	private String text_content;

	public TextField(Long id, String content, TemplateField templateField) {
		super(id, content, templateField);
	}

	public TextField() {
	}

	public void setId(Long id) {
		this.id = id;
	}
	public Long getId() {
		return id;
	}

	@Override
	public String getContent() {
		return text_content;
	}
	@Override
	public void setContent(String content) {
		this.text_content = content;
	}
}
