package gal.udc.fic.prperez.pleste.service.dao.component;

import gal.udc.fic.prperez.pleste.service.dao.template.TemplateField;
import jakarta.persistence.*;

import java.util.Objects;

@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(discriminatorType = DiscriminatorType.STRING)
public abstract class Field<T> {
	@Id
	@GeneratedValue
	private Long id;
	@ManyToOne(optional = false)
	private TemplateField templateField;

	public Field(Long id, T content, TemplateField templateField) {
		this.id = id;
		setContent(content);
		this.templateField = templateField;
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

	public abstract T getContent();
	public abstract void setContent(T content);

	public TemplateField getTemplateField() {
		return templateField;
	}
	public void setTemplateField(TemplateField templateField) {
		this.templateField = templateField;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (!(o instanceof Field<?> field)) return false;
		return Objects.equals(id, field.id);
	}

	@Override
	public int hashCode() {
		return Objects.hash(id);
	}
}
