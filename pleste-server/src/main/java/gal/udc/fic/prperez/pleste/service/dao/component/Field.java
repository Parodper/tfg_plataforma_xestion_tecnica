package gal.udc.fic.prperez.pleste.service.dao.component;

import gal.udc.fic.prperez.pleste.service.dao.template.TemplateField;
import jakarta.persistence.*;

@Entity
public class Field {
	@Id
	@GeneratedValue
	private Long id;
	private String content;
	@OneToOne
	private TemplateField templateField;
	@ManyToOne
	private Component link;
}
