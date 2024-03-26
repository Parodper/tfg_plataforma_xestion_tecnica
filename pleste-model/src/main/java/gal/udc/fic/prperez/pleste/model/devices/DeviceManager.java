package gal.udc.fic.prperez.pleste.model.devices;

import gal.udc.fic.prperez.pleste.model.devices.dao.SQLDeviceDaoImpl;
import gal.udc.fic.prperez.pleste.model.devices.exceptions.DeviceAlreadyExistsException;
import gal.udc.fic.prperez.pleste.model.devices.exceptions.DeviceNotFoundException;

import java.sql.Connection;
import java.util.List;

public class DeviceManager {
	private final SQLDeviceDaoImpl deviceDatabase;
	Connection connection = null;

	public DeviceManager() {
		deviceDatabase = new SQLDeviceDaoImpl();
	}

	public Long addDevice(Device d) throws DeviceAlreadyExistsException {
		return deviceDatabase.create(connection, d).getId();
	}

	public void removeDevice(Long id) throws DeviceNotFoundException {
		deviceDatabase.delete(connection, id);
	}

	public List<Device> getDevicesByName(String name) {
		return deviceDatabase.getAllDevices().stream().filter((x) -> x.getName().equals(name)).toList();
	}

	public Device getDevice(Long id) throws DeviceNotFoundException {
		return deviceDatabase.getById(id);
	}

	public void modifyDevice(Long id, Device device) throws DeviceNotFoundException {
		deviceDatabase.update(connection, id, device);
	}
}
