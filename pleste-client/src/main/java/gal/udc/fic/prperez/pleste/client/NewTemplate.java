package gal.udc.fic.prperez.pleste.client;

import org.openapitools.client.ApiException;
import org.openapitools.client.api.DefaultApi;
import org.openapitools.client.model.Template;
import org.openapitools.client.model.TemplateField;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Map;
import java.util.HashMap;

@Controller
public class NewTemplate {
	private DefaultApi defaultApi;

	public @Autowired NewTemplate(DefaultApi defaultApi) {
		this.defaultApi = defaultApi;
	}

	@GetMapping("/newtemplate")
	public String newTemplate() {
		//TODO: Autocompletar campos
		return "newtemplate.html";
	}

	@PostMapping(value = "/newtemplate")
	public ResponseEntity<String> createNewTemplate(@RequestBody String name) throws URISyntaxException {
		//Parse POST into a Template
		Template template = new Template();
		Map<String, String> parameters = new HashMap<>();

		for(String parameter : name.split("&")) {
			parameters.put(parameter.split("=")[0], parameter.split("=")[1]);
		}

		template.setName(parameters.get("template_name"));
		template.setDescription(parameters.get("template_description"));

		for(String key : parameters.keySet()) {
			if(key.matches("^field_[0-9]{3}") && !parameters.get(key).isEmpty()) {
				String id = key.substring(6, 6 + 4);
				TemplateField field = new TemplateField();

				field.setName(parameters.get("field_" + id + "_name"));
				field.setMandatory(Boolean.parseBoolean(parameters.get("field_" + id + "_mandatory")));
				field.setType(TemplateField.TypeEnum.valueOf(parameters.get("field_" + id + "_type")));

				template.getFields().add(field);
			}
		}

		try {
			defaultApi.addTemplate(template);
		} catch (ApiException e) {
			if(e.getCode() == 409) {
				return ResponseEntity.ok().body("Template already exists");
			} else {
				throw new RuntimeException(e);
			}
		}

		return ResponseEntity.created(new URI("/newtemplate")).body("Created template 0");
	}
}
