package gal.udc.fic.prperez.pleste.service.dao.users;

import jakarta.persistence.*;

@Entity
public class User {
	@Id
	@GeneratedValue
	private Long id;
	private String username;
	private String email;
	@OneToOne(cascade = CascadeType.ALL)
	private Password password;
	private Roles role;

	public User() {}

	public User(Long id, String username, String email, Password password, Roles role) {
		this.id = id;
		this.username = username;
		this.email = email;
		this.password = password;
		this.role = role;
	}

	public User(Long id) {
		this.id = id;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public Password getPassword() {
		return password;
	}

	public void setPassword(Password password) {
		this.password = password;
	}

	public Roles getRole() {
		return role;
	}

	public void setRole(Roles role) {
		this.role = role;
	}
}
