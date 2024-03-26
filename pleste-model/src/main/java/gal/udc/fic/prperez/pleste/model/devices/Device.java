package gal.udc.fic.prperez.pleste.model.devices;

import java.util.List;
import java.util.Objects;

public class Device {
	public static Long INVALID_ID = -1L;

	private Long id;
	private String config;
	private String name;
	private String location;
	private String owner;
	private List<String> users;

	public Device(Long id, String config, String name, String location, String owner, List<String> users) {
		this.id = id;
		this.config = config;
		this.name = name;
		this.location = location;
		this.owner = owner;
		this.users = users;
	}

	public Device(Long id) {
		this.id = id;
		this.config = null;
		this.name = null;
		this.location = null;
		this.owner = null;
		this.users = null;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		Device device = (Device) o;
		return Objects.equals(id, device.getId());
	}

	@Override
	public int hashCode() {
		return Objects.hash(id);
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getConfig() {
		return config;
	}

	public void setConfig(String config) {
		this.config = config;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getLocation() {
		return location;
	}

	public void setLocation(String location) {
		this.location = location;
	}

	public String getOwner() {
		return owner;
	}

	public void setOwner(String owner) {
		this.owner = owner;
	}

	public List<String> getUsers() {
		return users;
	}

	public void setUsers(List<String> users) {
		this.users = users;
	}
}
