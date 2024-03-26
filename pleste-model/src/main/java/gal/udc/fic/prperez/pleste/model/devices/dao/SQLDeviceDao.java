package gal.udc.fic.prperez.pleste.model.devices.dao;

import gal.udc.fic.prperez.pleste.model.devices.Device;
import gal.udc.fic.prperez.pleste.model.devices.exceptions.DeviceAlreadyExistsException;
import gal.udc.fic.prperez.pleste.model.devices.exceptions.DeviceNotFoundException;

import java.sql.Connection;
import java.util.List;

public interface SQLDeviceDao {
	public void create(Connection connection, Device device) throws DeviceAlreadyExistsException;
	public void delete(Connection connection, long id) throws DeviceNotFoundException;
	public void update(Connection connection, long id, Device device) throws DeviceNotFoundException;
	public Device getById(long id) throws DeviceNotFoundException;
	public List<Device> getAllDevices();
}
