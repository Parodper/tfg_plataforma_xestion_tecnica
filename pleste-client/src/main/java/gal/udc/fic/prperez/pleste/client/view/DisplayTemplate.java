package gal.udc.fic.prperez.pleste.client.view;

import gal.udc.fic.prperez.pleste.client.exceptions.ObjectNotFoundException;
import jakarta.servlet.http.HttpSession;
import org.openapitools.client.ApiException;
import org.openapitools.client.api.DefaultApi;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class DisplayTemplate {
	private final DefaultApi defaultApi;

	public @Autowired DisplayTemplate(DefaultApi defaultApi) {
		this.defaultApi = defaultApi;
	}

	@GetMapping("/template")
	public String displayTemplate(@RequestParam(name = "id") String idPath, Model model, HttpSession session) {
		CommonView.setModel(model, session);
		try {
			model.addAttribute("template", defaultApi.getTemplate(idPath));
			model.addAttribute("components", defaultApi.getTemplateComponents(idPath));
		} catch (ApiException e) {
			if(e.getCode() == 404) {
				throw new ObjectNotFoundException("modelo", idPath);
			} else {
				throw new RuntimeException(e);
			}
		}

		return "displaytemplate.html";
	}
}
