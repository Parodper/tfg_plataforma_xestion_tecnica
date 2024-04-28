package gal.udc.fic.prperez.pleste.client.view;

import gal.udc.fic.prperez.pleste.client.exceptions.InternalErrorException;
import gal.udc.fic.prperez.pleste.client.exceptions.MissingMandatoryFieldException;
import gal.udc.fic.prperez.pleste.client.exceptions.ObjectNotFoundException;
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
import org.springframework.web.util.HtmlUtils;

import java.net.*;
import java.nio.charset.Charset;
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

	private static String encodeURI(String text) {
		return URLEncoder.encode(text, Charset.defaultCharset());
	}

	private static String decodeURI(String text) {
		return URLDecoder.decode(text, Charset.defaultCharset());
	}

	private static String encodeHTML(String text) {
		return HtmlUtils.htmlEscape(text);
	}

	@GetMapping("/newcomponent")
	public String newComponent(@RequestParam(name = "template") String templateIdParam, @RequestParam(name = "name", required = false) String nameParam, @RequestParam(name = "description", required = false) String descriptionParam, Model model) {
		String templateId = decodeURI(templateIdParam);
		String name = nameParam == null ? "" : decodeURI(nameParam);
		String description = descriptionParam == null ? "" : decodeURI(descriptionParam);

		model.addAttribute("component_name", encodeHTML(name));
		model.addAttribute("component_description", encodeHTML(description));
		model.addAttribute("selected_template", encodeHTML(templateIdParam));

		try {
			model.addAttribute("templates", defaultApi.getAllTemplates());
			model.addAttribute("fields", defaultApi.getTemplate(templateId).getFields());
		} catch (ApiException e) {
			if(e.getCode() == HttpStatus.NOT_FOUND.value()) {
				throw new ObjectNotFoundException(templateId + " not found");
			} else {
				throw new InternalErrorException(e.getMessage());
			}
		}

		return "newcomponent.html";
	}

	@PostMapping(value = "/newcomponent")
	public ResponseEntity<String> createNewTemplate(
			@RequestParam(name = "template") String templateIdParam,
			@RequestParam(name = "component_name") String nameParam,
			@RequestParam(name = "component_description", required = false) String descriptionParam,
			@RequestParam Map<String, String> fields) {
		Long newId;
		Template template;
		Component component = new Component();
		Map<String, String> fieldMap = new HashMap<>();

		String name = decodeURI(nameParam);
		String description = decodeURI(descriptionParam);

		component.setName(name);
		component.setDescription(description);

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

					if (templateField.getType().equals(TemplateField.TypeEnum.LINK)) {
						if(fieldMap.get(templateField.getName()).isEmpty()) {

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
				.body("Created template #" + newId);
	}
}
