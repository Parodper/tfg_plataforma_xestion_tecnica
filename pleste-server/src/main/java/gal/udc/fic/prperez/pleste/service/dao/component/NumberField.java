package gal.udc.fic.prperez.pleste.service.dao.component;

import gal.udc.fic.prperez.pleste.service.dao.template.TemplateField;
import jakarta.persistence.*;

import java.math.BigDecimal;

@Entity
@DiscriminatorValue("NUMBER")
public class NumberField extends Field<BigDecimal> {
	@Id
	private Long id;

	private BigDecimal number_content;

	public NumberField(Long id, BigDecimal content, TemplateField templateField) {
		super(id, content, templateField);
	}

	public NumberField() {
	}

	public void setId(Long id) {
		this.id = id;
	}
	public Long getId() {
		return id;
	}

	@Override
	public BigDecimal getContent() {
		return number_content;
	}
	@Override
	public void setContent(BigDecimal content) {
		this.number_content = content;
	}
}
