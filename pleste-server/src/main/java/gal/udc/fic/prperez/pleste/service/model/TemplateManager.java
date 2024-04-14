package gal.udc.fic.prperez.pleste.service.model;

import gal.udc.fic.prperez.pleste.service.dao.template.SQLTemplateDao;
import gal.udc.fic.prperez.pleste.service.model.exceptions.TemplateAlreadyExistsException;
import gal.udc.fic.prperez.pleste.service.model.exceptions.TemplateNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.NoSuchElementException;

@Transactional
@Service
public class TemplateManager {
	private final SQLTemplateDao deviceDatabase;

	public @Autowired TemplateManager(SQLDeviceDao deviceDatabase) {
		this.deviceDatabase = deviceDatabase;
	}

	public Long addDevice(String name, String location) throws TemplateAlreadyExistsException {
		Device d = new Device();

		d.setName(name);
		d.setLocation(location);

		return deviceDatabase.save(d).getId();
	}

	public void removeDevice(Long id) throws TemplateNotFoundException {
		if(deviceDatabase.existsById(id)) {
			deviceDatabase.delete(new Device(id));
		} else {
			throw new TemplateNotFoundException(id, "none");
		}
	}

	public List<Device> getDevicesByName(String name) {
		return deviceDatabase.findAll().stream().filter((x) -> x.getName().equals(name)).toList();
	}

	public Device getDevice(Long id) throws TemplateNotFoundException {
		Optional<Device> d;
		try {
			d = deviceDatabase.findById(id);
			if(d.isPresent()) {
				return d.get();
			} else {
				throw new TemplateNotFoundException(id, "none");
			}
		} catch (NoSuchElementException e) {
			throw new TemplateNotFoundException(id, "none");
		}
	}

	public void modifyDevice(Device device) throws TemplateNotFoundException {
		deviceDatabase.save(device);
	}
}
