package gal.udc.fic.prperez.pleste.service;

import gal.udc.fic.prperez.pleste.service.devices.dao.Device;
import gal.udc.fic.prperez.pleste.service.devices.DeviceManager;
import gal.udc.fic.prperez.pleste.service.devices.exceptions.DeviceAlreadyExistsException;
import gal.udc.fic.prperez.pleste.service.devices.exceptions.DeviceNotFoundException;
import gal.udc.fic.prperez.pleste.service.api.DevicesApi;
import gal.udc.fic.prperez.pleste.service.model.NewDevice;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class DeviceServiceImpl implements DevicesApi {
	private final DeviceManager deviceManager;

	public @Autowired DeviceServiceImpl(DeviceManager deviceManager) {
		this.deviceManager = deviceManager;
	}

	@Override
	public ResponseEntity<Void> devicesDeviceIdDelete(Object deviceId) {
		try {
			deviceManager.removeDevice(Long.parseLong((String) deviceId));
			return ResponseEntity.noContent().build();
		} catch (DeviceNotFoundException e) {
			return ResponseEntity.notFound().build();
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
		}
	}

	@Override
	public ResponseEntity<Device> devicesDeviceIdGet(Object deviceId) {
		try {
			return ResponseEntity.ok(deviceManager.getDevice(Long.parseLong((String) deviceId)));
		} catch (DeviceNotFoundException e) {
			return ResponseEntity.notFound().build();
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
		}
	}

	@Override
	public ResponseEntity<Void> devicesDeviceIdPost(Object deviceId, Device device) {
		try {
			if(deviceId.equals(device.getId())) {
				deviceManager.modifyDevice(device);
				return ResponseEntity.noContent().build();
			} else {
				throw new DeviceNotFoundException((Long) deviceId, device.getName());
			}
		} catch (DeviceNotFoundException e) {
			return ResponseEntity.notFound().build();
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
		}
	}

	@Override
	public ResponseEntity<Integer> devicesPost(NewDevice newDevice) {
		try {
			deviceManager.addDevice(newDevice.getName(), newDevice.getLocation());
			return ResponseEntity.noContent().build();
		} catch (DeviceAlreadyExistsException e) {
			return ResponseEntity.badRequest().build();
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
		}
	}
}
