package gal.udc.fic.prperez.pleste.service;

import gal.udc.fic.prperez.pleste.service.api.TemplateApi;
import gal.udc.fic.prperez.pleste.service.model.TemplateManager;
import gal.udc.fic.prperez.pleste.service.model.exceptions.TemplateAlreadyExistsException;
import gal.udc.fic.prperez.pleste.service.model.exceptions.TemplateNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TemplateServiceImpl implements TemplateApi {
	private final TemplateManager templateManager;

	public @Autowired TemplateServiceImpl(TemplateManager templateManager) {
		this.templateManager = templateManager;
	}

	@Override
	public ResponseEntity<Void> devicesDeviceIdDelete(Object deviceId) {
		try {
			templateManager.removeDevice(Long.parseLong((String) deviceId));
			return ResponseEntity.noContent().build();
		} catch (TemplateNotFoundException e) {
			return ResponseEntity.notFound().build();
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
		}
	}

	@Override
	public ResponseEntity<Device> devicesDeviceIdGet(Object deviceId) {
		try {
			return ResponseEntity.ok(templateManager.getDevice(Long.parseLong((String) deviceId)));
		} catch (TemplateNotFoundException e) {
			return ResponseEntity.notFound().build();
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
		}
	}

	@Override
	public ResponseEntity<Void> devicesDeviceIdPost(Object deviceId, Device device) {
		try {
			if(deviceId.equals(device.getId())) {
				templateManager.modifyDevice(device);
				return ResponseEntity.noContent().build();
			} else {
				throw new TemplateNotFoundException((Long) deviceId, device.getName());
			}
		} catch (TemplateNotFoundException e) {
			return ResponseEntity.notFound().build();
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
		}
	}

	@Override
	public ResponseEntity<Integer> devicesPost(NewDevice newDevice) {
		try {
			Integer id = Math.toIntExact(templateManager.addDevice(newDevice.getName(), newDevice.getLocation()));
			return ResponseEntity.ok(id);
		} catch (TemplateAlreadyExistsException e) {
			return ResponseEntity.badRequest().build();
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
		}
	}
}
