package gal.udc.fic.prperez.pleste.client.view;

import gal.udc.fic.prperez.pleste.client.exceptions.InternalErrorException;
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
		return switch (type) {

			case "template" -> deleteTemplate(id);
			case "component" -> deleteComponent(id);
			default -> ResponseEntity
					.status(HttpStatus.BAD_REQUEST)
					.body("Non se recoñece o tipo " + type + ".");
		};
	}

	private ResponseEntity<String> deleteTemplate(String templateId) {
		try {
			defaultApi.removeTemplate(templateId);

			return ResponseEntity
					.status(HttpStatus.SEE_OTHER)
					.header("Location","/")
					.body("Eliminado nº" + templateId + ".");
		} catch(ApiException e) {
			return switch (HttpStatus.valueOf(e.getCode())) {
				case CONFLICT -> ResponseEntity
						.status(HttpStatus.CONFLICT)
						.body("O modelo nº" + templateId + " inda está en uso. Elimine antes todos os compoñentes que a referencian.");
				case NOT_FOUND -> ResponseEntity
						.status(HttpStatus.NOT_FOUND)
						.body("Non se atopou o modelo nº" + templateId + ".");
				default -> throw new InternalErrorException(e.getMessage());
			};
		}
	}

	private ResponseEntity<String> deleteComponent(String componentId) {
		try {
			defaultApi.removeComponent(componentId);

			return ResponseEntity
					.status(HttpStatus.SEE_OTHER)
					.header("Location","/")
					.body("Eliminado nº" + componentId + ".");
		} catch(ApiException e) {
			if (HttpStatus.valueOf(e.getCode()).equals(HttpStatus.NOT_FOUND)) {
				return ResponseEntity
						.status(HttpStatus.NOT_FOUND)
						.body("Non se atopou o compoñente nº" + componentId + ".");
			} else {
				throw new InternalErrorException(e.getMessage());
			}
		}
	}
}
