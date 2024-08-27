package gal.udc.fic.prperez.pleste.client.view;

import gal.udc.fic.prperez.pleste.client.exceptions.InternalErrorException;
import gal.udc.fic.prperez.pleste.client.exceptions.ObjectNotFoundException;
import jakarta.servlet.http.HttpSession;
import org.openapitools.client.ApiException;
import org.openapitools.client.api.DefaultApi;
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
import java.util.List;
import java.util.Map;

@Controller
public class EditTemplate {
	private final DefaultApi defaultApi;

	public @Autowired EditTemplate(DefaultApi defaultApi) {
		this.defaultApi = defaultApi;
	}

	@GetMapping("/edittemplate")
	public String editTemplate(@RequestParam(name = "id") String idParam, Model model, HttpSession session) {
		CommonView.setModel(model, session);

		try {
			model.addAttribute("template", defaultApi.getTemplate(idParam));
			model.addAttribute("fieldsdisabled", !defaultApi.getTemplateComponents(idParam).isEmpty());
		} catch (ApiException e) {
			if (e.getCode() == HttpStatus.NOT_FOUND.value()) {
				throw new ObjectNotFoundException("modelo", idParam);
			} else {
				throw new InternalErrorException(e.getMessage());
			}
		}
		return "edittemplate.html";
	}

	@PostMapping(value = "/edittemplate", consumes = "application/x-www-form-urlencoded;charset=UTF-8")
	public ResponseEntity<String> updateTemplate(@RequestParam(name = "id") String idParam, @RequestParam Map<String, String> parameters) {
		Template template;
		List<TemplateField> fields;
		List<String> fieldNames = new ArrayList<>();

		try {
			template = defaultApi.getTemplate(idParam);
			fields = template.getFields();
			template.setName(parameters.get("template_name"));
			template.setDescription(parameters.get("template_description"));

			if(defaultApi.getTemplateComponents(idParam).isEmpty()) {
				for(String key : parameters.keySet()) {
					if(key.matches("^field_[0-9]{3}_name") && !parameters.get(key).isEmpty()) {
						String id = key.substring("field_".length(), "field_".length() + 3);
						String name = parameters.get(key);
						fieldNames.add(name);

						TemplateField field = fields.stream().filter(f -> f.getName().equals(name)).findFirst().orElse(new TemplateField().name(name));

						field.setMandatory(Boolean.parseBoolean(parameters.get("field_" + id + "_mandatory")));

						switch (parameters.get("field_" + id + "_type")) {
							case "Ligazón":
								field.setType(TemplateField.TypeEnum.LINK);
								break;
							case "Data":
								field.setType(TemplateField.TypeEnum.DATETIME);
								break;
							case "Texto libre":
								field.setType(TemplateField.TypeEnum.TEXT);
								break;
							case "Número":
								field.setType(TemplateField.TypeEnum.NUMBER);
							default:
								field.setType(TemplateField.TypeEnum.TEXT);
						}

						if(field.getId() == null) {
							defaultApi.addFieldTemplate(template.getId().toString(), field);
						} else {
							defaultApi.modifyFieldTemplate(template.getId().toString(), field.getId().toString(), field);
						}
					}
				}

				for(TemplateField oldField : template.getFields()) {
					if(!fieldNames.contains(oldField.getName())) {
						defaultApi.removeTemplateField(template.getId().toString(), oldField.getId().toString());
					}
				}
			}

			defaultApi.modifyTemplate(template.getId().toString(), template);
		} catch (ApiException e) {
			if (e.getCode() == HttpStatus.NOT_FOUND.value()) {
				throw new ObjectNotFoundException("modelo", idParam);
			} else {
				throw new InternalErrorException(e.getMessage());
			}
		}

		return ResponseEntity
				.status(HttpStatus.SEE_OTHER)
				.header("Location","/template?id=" + idParam)
				.body("Created template #" + idParam);
	}
}
