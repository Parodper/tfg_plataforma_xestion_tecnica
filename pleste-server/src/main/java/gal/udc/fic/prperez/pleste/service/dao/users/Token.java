package gal.udc.fic.prperez.pleste.service.dao.users;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;

import java.util.Objects;

@Entity
public class Token {
	@Id
	private Long id;
	@ManyToOne
	private User user;
	private String token;

	public Token() {}

	public Token(Long id, User user, String token) {
		this.id = id;
		this.user = user;
		this.token = token;
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

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (!(o instanceof Token token)) return false;
		return Objects.equals(id, token.id);
	}

	@Override
	public int hashCode() {
		return Objects.hashCode(id);
	}
}
