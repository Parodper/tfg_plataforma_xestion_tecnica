package gal.udc.fic.prperez.pleste.service.dao.component;

import gal.udc.fic.prperez.pleste.service.dao.template.TemplateField;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;

@Entity
@DiscriminatorValue("LINK")
public class LinkField extends Field<Component> {
	@Id
	private Long id;

	@ManyToOne()
	private Component link_content;

	public LinkField(Long id, Component content, TemplateField templateField) {
		super(id, content, templateField);
	}

	public LinkField() {}

	public void setId(Long id) {
		this.id = id;
	}
	public Long getId() {
		return id;
	}

	@Override
	public Component getContent() {
		return link_content;
	}
	@Override
	public void setContent(Component content) {
		this.link_content = content;
	}
}
