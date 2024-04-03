package gal.udc.fic.prperez.pleste.model.devices;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;

import java.util.Objects;

@Entity
public class Device {
	public static Long INVALID_ID = -1L;

	@Id
	@GeneratedValue
	private Long id;

	private String config;

	private String name;

	private String location;

	private String owner;

	public Device(Long id, String config, String name, String location, String owner) {
		this.id = id;
		this.config = config;
		this.name = name;
		this.location = location;
		this.owner = owner;
	}

	public Device(Long id) {
		this.id = id;
		this.config = null;
		this.name = null;
		this.location = null;
		this.owner = null;
	}

	public Device() {

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

}
