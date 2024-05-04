package gal.udc.fic.prperez.pleste.service.dao.users;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.OneToOne;

@Entity
public class Password {
	@Id
	private Long id;
	@OneToOne
	private User user;
	private String password;

	public Password() {}

	public Password(Long id, User user, String password) {
		this.id = id;
		this.user = user;
		this.password = password;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}
}
