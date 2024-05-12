package gal.udc.fic.prperez.pleste.client.view;

import gal.udc.fic.prperez.pleste.client.exceptions.InternalErrorException;
import gal.udc.fic.prperez.pleste.client.exceptions.ObjectNotFoundException;
import jakarta.servlet.http.HttpSession;
import org.openapitools.client.ApiException;
import org.openapitools.client.api.DefaultApi;
import org.openapitools.client.model.Component;
import org.openapitools.client.model.Field;
import org.openapitools.client.model.Template;
import org.openapitools.client.model.TemplateField;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.HashMap;
import java.util.Map;

@Controller
public class DisplayComponent {
	private final DefaultApi defaultApi;

	public @Autowired DisplayComponent(DefaultApi defaultApi) {
		this.defaultApi = defaultApi;
	}

	@GetMapping("/component")
	public String displayComponent(@RequestParam(name = "id") String idPath, Model model, HttpSession session) {
		long id = Long.parseLong(idPath);
		CommonView.setModel(model, session);

		try {
			Template template;
			Component component = defaultApi.getComponent(idPath);
			template = defaultApi.getTemplate(component.getTemplate().getId().toString());
			Map<Long, String> links = new HashMap<>();

			for(Field f : component.getFields()) {
				if(f.getTemplateField().getType() == TemplateField.TypeEnum.LINK) {
					if(f.getLink() == null) {
						links.put(f.getLink(), "");
					} else {
						if(!links.containsKey(f.getLink())) {
							try {
								links.put(f.getLink(), defaultApi.getComponent(f.getLink().toString()).getName());
							} catch (ApiException e) {
								links.put(f.getLink(), f.getLink().toString());
							}
						}
					}
				}
			}

			model.addAttribute("component", component);
			model.addAttribute("template", template);
			model.addAttribute("links", links);
		} catch (ApiException e) {
			if(e.getCode() == 404) {
				throw new ObjectNotFoundException("Component not found");
			} else {
				throw new InternalErrorException(e.getMessage());
			}
		}

		return "displaycomponent.html";
	}
}
