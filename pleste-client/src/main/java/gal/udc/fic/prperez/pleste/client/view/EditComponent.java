package gal.udc.fic.prperez.pleste.client.view;

import gal.udc.fic.prperez.pleste.client.exceptions.InternalErrorException;
import gal.udc.fic.prperez.pleste.client.exceptions.MissingMandatoryFieldException;
import gal.udc.fic.prperez.pleste.client.exceptions.ObjectNotFoundException;
import jakarta.servlet.http.HttpSession;
import org.openapitools.client.ApiException;
import org.openapitools.client.api.DefaultApi;
import org.openapitools.client.model.Component;
import org.openapitools.client.model.Field;
import org.openapitools.client.model.TemplateField;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.net.URLDecoder;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

			model.addAttribute("component_name", component.getName());
			model.addAttribute("component_description", component.getDescription());
			model.addAttribute("component_id", componentIdPath);
			model.addAttribute("fields", component.getFields());
		} catch (ApiException e) {
			if(e.getCode() == HttpStatus.NOT_FOUND.value()) {
				throw new ObjectNotFoundException("Component" + componentIdPath + " not found");
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

			for(int i = 0; i < component.getFields().size(); i++) {
				Field field = component.getFields().get(i);
				String fieldName = field.getTemplateField().getName();
				field.setName(fieldName);

				if(field.getTemplateField().getMandatory() && (fieldMap.get(fieldName) == null || fieldMap.get(fieldName).isEmpty())) {
					missingFields.add(fieldName);
				} else {
					String value = fieldMap.get(fieldName);

					if (field.getTemplateField().getType().equals(TemplateField.TypeEnum.LINK)) {
						if(value == null || value.isEmpty()) {
							field.setLink(null);
						} else {
							field.setLink(
									new Component().id(
											Long.parseLong(value)));
						}
					} else {
						field.setContent(value);
					}
				}

				component.getFields().set(i, field);
			}

			if(!missingFields.isEmpty()) {
				throw new MissingMandatoryFieldException(missingFields);
			}

			defaultApi.modifyComponent(componentIdPath, component);
			for(Field field : component.getFields()) {
				defaultApi.modifyFieldComponent(componentIdPath, field.getName(),
						field.getTemplateField().getType().equals(TemplateField.TypeEnum.LINK) ? field.getLink().getId().toString() : field.getContent());
			}
		} catch (ApiException e) {
			if(e.getCode() == HttpStatus.NOT_FOUND.value()) {
				throw new ObjectNotFoundException("Component #" + componentIdPath + " not found");
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
