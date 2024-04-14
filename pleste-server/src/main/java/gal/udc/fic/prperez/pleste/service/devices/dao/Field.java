package gal.udc.fic.prperez.pleste.service.devices.dao;

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
