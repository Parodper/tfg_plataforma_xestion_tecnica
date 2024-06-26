package gal.udc.fic.prperez.pleste.service.dao.template;

import jakarta.persistence.*;

import java.util.Objects;

@Entity
public class TemplateField {
	@Id
	@GeneratedValue
	private Long id;
	private String name;
	private boolean mandatory;
	private FieldTypes type;

	public TemplateField(Long id, String name, boolean mandatory, FieldTypes type) {
		this.id = id;
		this.name = name;
		this.mandatory = mandatory;
		this.type = type;
	}

	public TemplateField(Long id) {
		this.id = id;
	}

	public TemplateField() {}

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

	public boolean isMandatory() {
		return mandatory;
	}

	public void setMandatory(boolean mandatory) {
		this.mandatory = mandatory;
	}

	public FieldTypes getType() {
		return type;
	}

	public void setType(FieldTypes type) {
		this.type = type;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (!(o instanceof TemplateField that)) return false;
		return Objects.equals(id, that.id);
	}

	@Override
	public int hashCode() {
		return Objects.hashCode(id);
	}
}
