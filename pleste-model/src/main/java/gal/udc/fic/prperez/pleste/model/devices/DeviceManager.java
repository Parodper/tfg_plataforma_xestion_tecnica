package gal.udc.fic.prperez.pleste.model.devices;

import gal.udc.fic.prperez.pleste.model.devices.dao.SQLDeviceDao;
import gal.udc.fic.prperez.pleste.model.devices.exceptions.DeviceAlreadyExistsException;
import gal.udc.fic.prperez.pleste.model.devices.exceptions.DeviceNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;

import java.sql.Connection;
import java.util.List;

@Service
public class DeviceManager {
	@Autowired
	private SQLDeviceDao deviceDatabase;

	public DeviceManager() {
	}

	public Long addDevice(Device d) throws DeviceAlreadyExistsException {
		return deviceDatabase.save(d).getId();
	}

	public void removeDevice(Long id) throws DeviceNotFoundException {
		deviceDatabase.delete(new Device(id));
	}

	public List<Device> getDevicesByName(String name) {
		return deviceDatabase.findAll().stream().filter((x) -> x.getName().equals(name)).toList();
	}

	public Device getDevice(Long id) throws DeviceNotFoundException {
		return deviceDatabase.getById(id);
	}

	public void modifyDevice(Device device) throws DeviceNotFoundException {
		deviceDatabase.save(device);
	}
}
