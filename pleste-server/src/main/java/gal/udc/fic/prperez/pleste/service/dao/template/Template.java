package gal.udc.fic.prperez.pleste.service.dao.template;

import jakarta.persistence.*;

import java.util.Set;


@Entity
public class Template {
	@Id
	@GeneratedValue
	private Long id;
	private String name;
	private String description;
	@OneToMany
	private Set<TemplateField> fields;
}
