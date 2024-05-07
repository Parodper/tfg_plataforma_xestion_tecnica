package gal.udc.fic.prperez.pleste.client.view;

import gal.udc.fic.prperez.pleste.client.exceptions.InternalErrorException;
import gal.udc.fic.prperez.pleste.client.exceptions.MissingMandatoryFieldException;
import gal.udc.fic.prperez.pleste.client.exceptions.ObjectNotFoundException;
import jakarta.servlet.http.HttpSession;
import org.openapitools.client.ApiException;
import org.openapitools.client.api.DefaultApi;
import org.openapitools.client.model.Component;
import org.openapitools.client.model.Field;
import org.openapitools.client.model.Template;
import org.openapitools.client.model.TemplateField;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
public class NewComponent {
	private final DefaultApi defaultApi;

	public @Autowired NewComponent(DefaultApi defaultApi) {
		this.defaultApi = defaultApi;
	}

	@GetMapping("/newcomponent")
	public String newComponent(@RequestParam(name = "template") String templateIdParam, Model model, HttpSession session) {
		CommonView.setModel(model, session);
		model.addAttribute("selected_template", templateIdParam);

		try {
			model.addAttribute("templates", defaultApi.getAllTemplates());
			model.addAttribute("fields", defaultApi.getTemplate(templateIdParam).getFields());
		} catch (ApiException e) {
			if(e.getCode() == HttpStatus.NOT_FOUND.value()) {
				throw new ObjectNotFoundException(templateIdParam + " not found");
			} else {
				throw new InternalErrorException(e.getMessage());
			}
		}

		return "newcomponent.html";
	}

	@PostMapping(value = "/newcomponent", consumes = "application/x-www-form-urlencoded;charset=UTF-8")
	public ResponseEntity<String> createNewComponent(@RequestParam(name = "template") String templateIdParam, @RequestParam Map<String, String> fields) {
		Long newId;
		Template template;
		Component component = new Component();
		Map<String, String> fieldMap = new HashMap<>();

		component.setName(fields.get("component_name"));
		component.setDescription(fields.get("component_description"));

		for(String field : fields.keySet()) {
			if(field.matches("^field_.*")) {
				String fieldName = field.substring("field_".length());
				String fieldValue = fields.get(field);

				fieldMap.put(fieldName, fieldValue);
			}
		}

		try {
			List<String> missingFields = new ArrayList<>();
			List<Field> localFields = new ArrayList<>();
			template = defaultApi.getTemplate(templateIdParam);

			for(TemplateField templateField : template.getFields()) {
				if(
						(!fieldMap.containsKey(templateField.getName()) || fieldMap.get(templateField.getName()).isEmpty()) && templateField.getMandatory()) {
					missingFields.add(templateField.getName());
				} else {
					Field tmpField = new Field();
					tmpField.setTemplateField(new TemplateField().id(templateField.getId()));
					tmpField.setName(defaultApi.getFieldTemplate(template.getId().toString(), templateField.getId().toString()).getName());

					if (templateField.getType().equals(TemplateField.TypeEnum.LINK)) {
						if(fieldMap.get(templateField.getName()).isEmpty()) {
							tmpField.setLink(null);
						} else {
							tmpField.setLink(
									new Component().id(
											Long.parseLong(fieldMap.get(templateField.getName()))));
						}
					} else {
						tmpField.setContent(fieldMap.get(templateField.getName()));
					}
					localFields.add(tmpField);
				}
			}

			if(!missingFields.isEmpty()) {
				throw new MissingMandatoryFieldException(missingFields);
			}

			component.setFields(localFields);
			component.setTemplate( new Template().id(template.getId()) );
			newId = defaultApi.addComponent(component);
		} catch (ApiException e) {
			throw new InternalErrorException(e.getMessage());
		}

		return ResponseEntity
				.status(HttpStatus.SEE_OTHER)
				.header("Location","/component?id=" + newId)
				.body("Created component #" + newId);
	}
}
