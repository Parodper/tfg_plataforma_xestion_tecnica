package gal.udc.fic.prperez.pleste.client;

import gal.udc.fic.prperez.pleste.client.service.dto.FieldTypes;
import gal.udc.fic.prperez.pleste.client.service.dto.Template;
import gal.udc.fic.prperez.pleste.client.service.dto.TemplateField;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Map;
import java.util.HashMap;

@Controller
public class NewTemplate {
	@GetMapping("/newtemplate")
	public String newTemplate(Model model) {
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
				field.setType(FieldTypes.valueOf(parameters.get("field_" + id + "_type")));

				template.getFields().add(field);
			}
		}

		return ResponseEntity.created(new URI("/newtemplate")).body("Created template 0");
	}
}
