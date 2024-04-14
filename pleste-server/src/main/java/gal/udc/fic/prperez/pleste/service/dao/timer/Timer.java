package gal.udc.fic.prperez.pleste.service.dao.timer;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;

@Entity
public class Timer {
	@Id
	@GeneratedValue
	private Long id;
}
