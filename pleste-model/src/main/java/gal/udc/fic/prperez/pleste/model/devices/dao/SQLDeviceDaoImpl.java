package gal.udc.fic.prperez.pleste.model.devices.dao;

import gal.udc.fic.prperez.pleste.model.devices.Device;
import gal.udc.fic.prperez.pleste.model.devices.exceptions.DeviceNotFoundException;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class SQLDeviceDaoImpl implements SQLDeviceDao {
	List<Device> deviceList = new ArrayList<>();

	@Override
	public Device create(Connection connection, Device device) {
		/* Create "queryString". */
//		String queryString = "INSERT INTO Eventos"
//				+ " (nombre, descripcion, fecha, fecha_alta, duracion, cancelada, asisten, no_asisten)"
//				+ " VALUES (?, ?, ?, ?, ?, ?, 0, 0)";
//
//		try (PreparedStatement preparedStatement = connection.prepareStatement(
//				queryString, Statement.RETURN_GENERATED_KEYS)) {
//
//
//			int i = 1;
//			preparedStatement.setString(i++, device.getNombre());
//			preparedStatement.setString(i++, device.getDescripcion());
//			preparedStatement.setTimestamp(i++, Timestamp.valueOf(device.getFecha()));
//			preparedStatement.setTimestamp(i++, Timestamp.valueOf(device.getFechaAlta()));
//			preparedStatement.setLong(i++, device.getDuracion());
//			preparedStatement.setBoolean(i++, device.getCancelado());
//
//
//			preparedStatement.executeUpdate();
//
//
//			ResultSet resultSet = preparedStatement.getGeneratedKeys();
//
//			if (!resultSet.next()) {
//				throw new SQLException(
//						"JDBC driver did not return generated key.");
//			}
//			Long deviceId = resultSet.getLong(1);
//
//			/* Return device. */
//			return new Evento(deviceId, device.getNombre(),
//					device.getDescripcion(), device.getFecha(),
//					device.getFechaAlta(), device.getDuracion(), device.getCancelado(),
//					device.getEmpleadosAsisten(), device.getEmpleadosNoAsisten());
//
//		} catch (SQLException e) {
//			throw new RuntimeException(e);
//		}

		device.setId((long) deviceList.size());
		deviceList.add(device);
		return device;
	}

	@Override
	public void delete(Connection connection, long id) throws DeviceNotFoundException {
		if(deviceList.contains(new Device(id))) {
			deviceList.removeIf(d -> d.getId().equals(id));
		} else {
			throw new DeviceNotFoundException(id, "");
		}
	}

	@Override
	public Device update(Connection connection, long id, Device device) throws DeviceNotFoundException {
		if(deviceList.contains(new Device(id))) {
			deviceList.replaceAll( (x) -> x.equals(new Device(id)) ? device : x);
			return getById(id);
		} else {
			throw new DeviceNotFoundException(id, device.getName());
		}
	}

	@Override
	public Device getById(long id) throws DeviceNotFoundException {
		for(Device d : deviceList) {
			if(d.getId().equals(id)) {
				return d;
			}
		}

		throw new DeviceNotFoundException(id, "");
	}

	@Override
	public List<Device> getAllDevices() {
		return deviceList;
	}
}
