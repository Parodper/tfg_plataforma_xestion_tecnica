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

@Controller
public class NewTemplate {
	private final DefaultApi defaultApi;

	public @Autowired NewTemplate(DefaultApi defaultApi) {
		this.defaultApi = defaultApi;
	}

	@GetMapping("/newtemplate")
	public String newTemplate() {
		return "newtemplate.html";
	}

	@PostMapping(value = "/newtemplate", consumes = "application/x-www-form-urlencoded;charset=UTF-8")
	public ResponseEntity<String> createNewTemplate(@RequestParam Map<String, String> parameters) {
		Template template = new Template();
		Long newId;

		template.setName(parameters.get("template_name"));
		template.setDescription(parameters.get("template_description"));

		for(String key : parameters.keySet()) {
			if(key.matches("^field_[0-9]{3}_name") && !parameters.get(key).isEmpty()) {
				String id = key.substring("field_".length(), "field_".length() + 3);
				TemplateField field = new TemplateField();

				field.setName(parameters.get("field_" + id + "_name"));
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
