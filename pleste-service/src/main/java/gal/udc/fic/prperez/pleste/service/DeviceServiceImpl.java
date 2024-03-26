package gal.udc.fic.prperez.pleste.service;

import gal.udc.fic.prperez.pleste.model.devices.Device;
import gal.udc.fic.prperez.pleste.model.devices.DeviceManager;
import gal.udc.fic.prperez.pleste.model.devices.exceptions.DeviceAlreadyExistsException;
import gal.udc.fic.prperez.pleste.model.devices.exceptions.DeviceNotFoundException;
import gal.udc.fic.prperez.pleste.service.api.DevicesApi;
import gal.udc.fic.prperez.pleste.service.model.NewDevice;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;

@RestController
public class DeviceServiceImpl implements DevicesApi {
	DeviceManager deviceManager;

	public DeviceServiceImpl() {
		deviceManager = new DeviceManager();
		try {
			deviceManager.addDevice(new Device(-1L, "A", "B", "C", "D", new ArrayList<>()));
		} catch (DeviceAlreadyExistsException e) {
			throw new RuntimeException(e);
		}
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
			deviceManager.modifyDevice(Long.parseLong((String) deviceId), device);
			return ResponseEntity.noContent().build();
		} catch (DeviceNotFoundException e) {
			return ResponseEntity.notFound().build();
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
		}
	}

	@Override
	public ResponseEntity<Integer> devicesPost(NewDevice newDevice) {
		try {
			deviceManager.addDevice(new Device(Device.INVALID_ID, "", newDevice.getName(), newDevice.getLocation(), "", new ArrayList<>()));
			return ResponseEntity.noContent().build();
		} catch (DeviceAlreadyExistsException e) {
			return ResponseEntity.badRequest().build();
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
		}
	}
}
