package gal.udc.fic.prperez.pleste.client.view;

import gal.udc.fic.prperez.pleste.client.exceptions.BadRequestException;
import gal.udc.fic.prperez.pleste.client.exceptions.InternalErrorException;
import gal.udc.fic.prperez.pleste.client.exceptions.MissingMandatoryFieldException;
import gal.udc.fic.prperez.pleste.client.exceptions.ObjectNotFoundException;
import jakarta.servlet.http.HttpSession;
import org.openapitools.client.ApiException;
import org.openapitools.client.api.DefaultApi;
import org.openapitools.client.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.math.BigDecimal;
import java.net.URLDecoder;
import java.nio.charset.Charset;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeParseException;
import java.util.*;

@Controller
public class EditComponent {
	private final DefaultApi defaultApi;

	public @Autowired EditComponent(DefaultApi defaultApi) {
		this.defaultApi = defaultApi;
	}

	private static String decodeURI(String text) {
		return URLDecoder.decode(text, Charset.defaultCharset());
	}

	@GetMapping("/editcomponent")
	public String editComponent(@RequestParam(name = "id") String componentIdPath, Model model, HttpSession session) {
		CommonView.setModel(model, session);
		try {
			Component component = defaultApi.getComponent(componentIdPath);
			Map<String, String> fieldValues = new HashMap<>();

			for(FieldObject f : component.getFields()) {
				try {
					switch (f.getTemplateField().getType()) {
						case TEXT -> fieldValues.put(f.getTemplateField().getName(), f.getContent().getString());
						case DATETIME -> fieldValues.put(f.getTemplateField().getName(),
								f.getContent().getJSONDatetime().getDatetime().toLocalDateTime().toString());
						case LINK -> fieldValues.put(f.getTemplateField().getName(),
								f.getContent().getComponent().getId().toString());
						case NUMBER ->
								fieldValues.put(f.getTemplateField().getName(), f.getContent().getBigDecimal().toString());
					}
				} catch (NullPointerException ignore) {
					fieldValues.put(f.getTemplateField().getName(), "");
				}
			}

			model.addAttribute("component_name", component.getName());
			model.addAttribute("component_description", component.getDescription());
			model.addAttribute("component_id", componentIdPath);
			model.addAttribute("fields", component.getFields());
			model.addAttribute("values", fieldValues);
		} catch (ApiException e) {
			if(e.getCode() == HttpStatus.NOT_FOUND.value()) {
				throw new ObjectNotFoundException("compoñente", componentIdPath);
			} else {
				throw new InternalErrorException(e.getMessage());
			}
		}

		return "editcomponent.html";
	}

	@PostMapping(value = "/editcomponent")
	public ResponseEntity<String> editComponent(@RequestParam(name = "id") String componentIdPath,
	                                            @RequestParam(name = "component_name") String nameParam,
	                                            @RequestParam(name = "component_description", required = false) String descriptionParam,
	                                            @RequestParam Map<String, String> fields) {
		Component component;
		Map<String, String> fieldMap = new HashMap<>();
		List<String> missingFields = new ArrayList<>();

		for(String field : fields.keySet()) {
			if(field.matches("^field_.*")) {
				String fieldName = field.substring("field_".length());
				String fieldValue = fields.get(decodeURI(field));

				fieldMap.put(fieldName, fieldValue);
			}
		}

		try {
			component = defaultApi.getComponent(componentIdPath);

			component.setDescription(decodeURI(descriptionParam));
			component.setName(decodeURI(nameParam));

			for(FieldObject fieldObject : component.getFields()) {
				String fieldName = fieldObject.getTemplateField().getName();
				String fieldValue = fieldMap.get(fieldName);

				FieldObjectContent value = null;

				if(fieldValue == null) {
					if (fieldObject.getTemplateField().getMandatory()) {
						missingFields.add(fieldName);
					} else {
						value = new FieldObjectContent();
					}
				} else {
					if(fieldValue.isEmpty()) {
						if(fieldObject.getTemplateField().getMandatory()) {
							missingFields.add(fieldName);
						} else {
							value = new FieldObjectContent();
						}
					} else {
						try {
							value = switch (fieldObject.getType()) {
								case TEXT -> new FieldObjectContent(fieldValue);
								case NUMBER -> new FieldObjectContent(new BigDecimal(fieldValue));
								case LINK -> new FieldObjectContent(new Component().id(Long.parseLong(fieldValue)));
								case DATETIME -> new FieldObjectContent(
										new JSONDatetime().datetime(
												LocalDateTime.parse(fieldValue)
														.atOffset(ZoneOffset
																.systemDefault()
																.getRules()
																.getOffset(Instant.now()))));
							};
						} catch (DateTimeParseException e) {
							throw new BadRequestException(fieldValue + " isn't a valid date");
						} catch (NumberFormatException e) {
							throw new BadRequestException(fieldValue + " isn't a valid number");
						}
					}
				}

				if (value != null) {
					FieldObject field = component.getFields().stream()
							.filter(f -> f.getTemplateField().getName().equals(fieldName))
							.toList().get(0);
					field.setContent(value);
				}
			}

			if(!missingFields.isEmpty()) {
				throw new MissingMandatoryFieldException(missingFields);
			}

			defaultApi.modifyComponent(componentIdPath, component);
		} catch (ApiException e) {
			if(e.getCode() == HttpStatus.NOT_FOUND.value()) {
				throw new ObjectNotFoundException("compoñente", componentIdPath);
			} else {
				throw new InternalErrorException(e.getMessage());
			}
		}

		return ResponseEntity
				.status(HttpStatus.SEE_OTHER)
				.header("Location","/component?id=" + componentIdPath)
				.body("Component #" + componentIdPath + " modified");
	}
}
