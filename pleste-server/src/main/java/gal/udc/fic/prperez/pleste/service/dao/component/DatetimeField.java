package gal.udc.fic.prperez.pleste.service.dao.component;
import gal.udc.fic.prperez.pleste.service.dao.template.TemplateField;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;

import java.time.LocalDateTime;

@Entity
@DiscriminatorValue("DATETIME")
public class DatetimeField extends Field<LocalDateTime> {
	@Id
	private Long id;

	private LocalDateTime datetime_content;

	public DatetimeField(Long id, LocalDateTime content, TemplateField templateField) {
		super(id, content, templateField);
	}

	public DatetimeField() {}

	public void setId(Long id) {
		this.id = id;
	}
	public Long getId() {
		return id;
	}

	@Override
	public LocalDateTime getContent() {
		return datetime_content;
	}
	@Override
	public void setContent(LocalDateTime content) {
		this.datetime_content = content;
	}
}
