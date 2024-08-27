package gal.udc.fic.prperez.pleste.service.dao.component;
import gal.udc.fic.prperez.pleste.service.JSONDatetime;
import gal.udc.fic.prperez.pleste.service.dao.template.FieldTypes;
import gal.udc.fic.prperez.pleste.service.dao.template.TemplateField;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;

@Entity
@DiscriminatorValue("DATETIME")
public class DatetimeField extends Field<JSONDatetime> {
	@Id
	private Long id;

	private JSONDatetime datetime_content;

	public DatetimeField(Long id, JSONDatetime content, TemplateField templateField) {
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
	public JSONDatetime getContent() {
		return datetime_content;
	}
	@Override
	public void setContent(JSONDatetime content) {
		this.datetime_content = content;
		super.content = content;
	}
	@Override
	protected FieldTypes getFieldType() {
		return FieldTypes.DATETIME;
	}
}
