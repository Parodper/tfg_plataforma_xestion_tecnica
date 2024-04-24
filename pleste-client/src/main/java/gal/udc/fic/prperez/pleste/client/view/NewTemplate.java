package gal.udc.fic.prperez.pleste.client.view;

import org.openapitools.client.ApiException;
import org.openapitools.client.api.DefaultApi;
import org.openapitools.client.model.Template;
import org.openapitools.client.model.TemplateField;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.HashMap;

@Controller
public class NewTemplate {
	private final DefaultApi defaultApi;

	public @Autowired NewTemplate(DefaultApi defaultApi) {
		this.defaultApi = defaultApi;
	}

	@GetMapping("/newtemplate")
	public String newTemplate() {
		//TODO: Autocompletar campos
		return "newtemplate.html";
	}

	@PostMapping(value = "/newtemplate")
	public ResponseEntity<String> createNewTemplate(@RequestBody String name) {
		//Parse POST into a Template
		Template template = new Template();
		Map<String, String> parameters = new HashMap<>();
		Long newId;

		for(String parameter : name.split("&")) {
			String[] keyValue = parameter.split("=");
			if(keyValue.length == 2) {
				parameters.put(keyValue[0], keyValue[1]);
			}
		}

		template.setName(parameters.get("template_name"));
		template.setDescription(parameters.get("template_description"));

		for(String key : parameters.keySet()) {
			if(key.matches("^field_[0-9]{3}_name") && !parameters.get(key).isEmpty()) {
				String id = key.substring("field_".length(), "field_".length() + 3);
				TemplateField field = new TemplateField();

				field.setName(parameters.get("field_" + id + "_name"));
				field.setMandatory(Boolean.parseBoolean(parameters.get("field_" + id + "_mandatory")));
				switch (parameters.get("field_" + id + "_type")) {
					case "Ligaz%C3%B3n":
						field.setType(TemplateField.TypeEnum.LINK);
						break;
					case "Data":
						field.setType(TemplateField.TypeEnum.DATETIME);
						break;
					case "Texto libre":
						field.setType(TemplateField.TypeEnum.TEXT);
						break;
					default:
						field.setType(TemplateField.TypeEnum.TEXT);
				}

				template.getFields().add(field);
			}
		}

		try {
			newId = defaultApi.addTemplate(template);
		} catch (ApiException e) {
			if(e.getCode() == 409) {
				return ResponseEntity.ok().body("Template already exists");
			} else {
				throw new RuntimeException(e);
			}
		}

		return ResponseEntity
				.status(HttpStatus.SEE_OTHER)
				.header("Location","/template?id=" + newId)
				.body("Created template #" + newId);
	}
}
