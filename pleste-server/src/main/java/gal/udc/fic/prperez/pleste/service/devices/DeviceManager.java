package gal.udc.fic.prperez.pleste.service.devices;

import gal.udc.fic.prperez.pleste.service.devices.dao.Device;
import gal.udc.fic.prperez.pleste.service.devices.dao.SQLDeviceDao;
import gal.udc.fic.prperez.pleste.service.devices.exceptions.DeviceAlreadyExistsException;
import gal.udc.fic.prperez.pleste.service.devices.exceptions.DeviceNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Transactional
@Service
public class DeviceManager {
	private final SQLDeviceDao deviceDatabase;

	public @Autowired DeviceManager(SQLDeviceDao deviceDatabase) {
		this.deviceDatabase = deviceDatabase;
	}

	public Long addDevice(String name, String location) throws DeviceAlreadyExistsException {
		Device d = new Device();

		d.setName(name);
		d.setLocation(location);

		return deviceDatabase.save(d).getId();
	}

	public void removeDevice(Long id) throws DeviceNotFoundException {
		deviceDatabase.delete(new Device(id));
	}

	public List<Device> getDevicesByName(String name) {
		return deviceDatabase.findAll().stream().filter((x) -> x.getName().equals(name)).toList();
	}

	public Device getDevice(Long id) throws DeviceNotFoundException {
		return deviceDatabase.findById(id).get();
	}

	public void modifyDevice(Device device) throws DeviceNotFoundException {
		deviceDatabase.save(device);
	}
}
