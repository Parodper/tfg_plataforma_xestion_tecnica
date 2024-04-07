package gal.udc.fic.prperez.pleste.service.devices;

import gal.udc.fic.prperez.pleste.service.devices.dao.Device;
import gal.udc.fic.prperez.pleste.service.devices.dao.SQLDeviceDao;
import gal.udc.fic.prperez.pleste.service.devices.exceptions.DeviceAlreadyExistsException;
import gal.udc.fic.prperez.pleste.service.devices.exceptions.DeviceNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

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
		if(deviceDatabase.existsById(id)) {
			deviceDatabase.delete(new Device(id));
		} else {
			throw new DeviceNotFoundException(id, "none");
		}
	}

	public List<Device> getDevicesByName(String name) {
		return deviceDatabase.findAll().stream().filter((x) -> x.getName().equals(name)).toList();
	}

	public Device getDevice(Long id) throws DeviceNotFoundException {
		Optional<Device> d;
		try {
			d = deviceDatabase.findById(id);
			if(d.isPresent()) {
				return d.get();
			} else {
				throw new DeviceNotFoundException(id, "none");
			}
		} catch (NoSuchElementException e) {
			throw new DeviceNotFoundException(id, "none");
		}
	}

	public void modifyDevice(Device device) throws DeviceNotFoundException {
		deviceDatabase.save(device);
	}
}
