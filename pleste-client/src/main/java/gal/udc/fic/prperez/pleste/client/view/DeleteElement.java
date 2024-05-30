package gal.udc.fic.prperez.pleste.client.view;

import gal.udc.fic.prperez.pleste.client.exceptions.BadRequestException;
import gal.udc.fic.prperez.pleste.client.exceptions.InternalErrorException;
import gal.udc.fic.prperez.pleste.client.exceptions.ObjectNotFoundException;
import org.openapitools.client.ApiException;
import org.openapitools.client.api.DefaultApi;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class DeleteElement {
	private final DefaultApi defaultApi;

	public @Autowired DeleteElement(DefaultApi defaultApi) {
		this.defaultApi = defaultApi;
	}

	@GetMapping("/deleteelement")
	public ResponseEntity<String> deleteElement(@RequestParam(name = "type") String type, @RequestParam(name = "id") String id) {
		switch (type) {
			case "template" -> deleteTemplate(id);
			case "component" -> deleteComponent(id);
			case "user" -> deleteUser(id);
			default -> throw new BadRequestException("Tipo non coñecido");
		}

		return ResponseEntity
				.status(HttpStatus.SEE_OTHER)
				.header("Location","/")
				.body("Eliminado nº" + id + ".");
	}

	private void deleteTemplate(String templateId) {
		try {
			defaultApi.removeTemplate(templateId);
		} catch(ApiException e) {
			switch (HttpStatus.valueOf(e.getCode())) {
				case BAD_REQUEST:
					throw new BadRequestException("O modelo inda ten compoñentes asociados");
				case NOT_FOUND:
					throw new ObjectNotFoundException("modelo", templateId);
				default:
					throw new InternalErrorException(e.getMessage());
			}
		}
	}

	private void deleteComponent(String componentId) {
		try {
			defaultApi.removeComponent(componentId);
		} catch(ApiException e) {
			if (HttpStatus.valueOf(e.getCode()).equals(HttpStatus.NOT_FOUND)) {
				throw new ObjectNotFoundException("compoñente", componentId);
			} else {
				throw new InternalErrorException(e.getMessage());
			}
		}
	}

	private void deleteUser(String componentId) {
		try {
			defaultApi.removeComponent(componentId);
		} catch(ApiException e) {
			if (HttpStatus.valueOf(e.getCode()).equals(HttpStatus.NOT_FOUND)) {
				throw new ObjectNotFoundException("usuario", componentId);
			} else {
				throw new InternalErrorException(e.getMessage());
			}
		}
	}
}
