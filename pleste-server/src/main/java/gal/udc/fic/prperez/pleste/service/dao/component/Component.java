package gal.udc.fic.prperez.pleste.service.dao.component;

import gal.udc.fic.prperez.pleste.service.dao.template.Template;
import jakarta.persistence.*;

import java.util.List;

@Entity
@Table(name = "components")
public class Component {
	@Id
	@GeneratedValue
	private Long id;
	@OneToOne
	private Template template;
	@OneToMany( fetch = FetchType.EAGER, cascade = CascadeType.ALL )
	private List<Field> fields;
}
