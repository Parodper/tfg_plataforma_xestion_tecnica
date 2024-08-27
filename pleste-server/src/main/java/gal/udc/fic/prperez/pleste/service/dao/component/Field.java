package gal.udc.fic.prperez.pleste.service.dao.component;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import gal.udc.fic.prperez.pleste.service.FieldDeserializer;
import gal.udc.fic.prperez.pleste.service.JSONDatetime;
import gal.udc.fic.prperez.pleste.service.JSONString;
import gal.udc.fic.prperez.pleste.service.dao.template.FieldTypes;
import gal.udc.fic.prperez.pleste.service.dao.template.TemplateField;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Objects;

@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(discriminatorType = DiscriminatorType.STRING)
@JsonDeserialize(using = FieldDeserializer.class)
public abstract class Field<T> {
	@Id
	@GeneratedValue
	private Long id;
	private FieldTypes type;
	@ManyToOne(optional = false)
	private TemplateField templateField;
	@Transient
	@Schema(anyOf = {JSONDatetime.class, Component.class, BigDecimal.class, String.class})
	protected T content;

	public Field(Long id, T content, TemplateField templateField) {
		this.id = id;
		setContent(content);
		this.templateField = templateField;
		this.type = getFieldType();
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

	public FieldTypes getType() {
		return type;
	}

	@JsonProperty
	public abstract T getContent();
	public abstract void setContent(T content);
	protected abstract FieldTypes getFieldType();

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
