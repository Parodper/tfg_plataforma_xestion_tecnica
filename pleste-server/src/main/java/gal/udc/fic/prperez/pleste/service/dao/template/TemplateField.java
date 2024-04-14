package gal.udc.fic.prperez.pleste.service.dao.template;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;

@Entity
public class TemplateField {
	@Id
	@GeneratedValue
	private Long id;
	private String name;
	private boolean mandatory;
	private FieldTypes type;
	private boolean multivalued;
}
