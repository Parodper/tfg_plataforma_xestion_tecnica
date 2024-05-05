package gal.udc.fic.prperez.pleste.client.view;

import gal.udc.fic.prperez.pleste.client.exceptions.InternalErrorException;
import org.openapitools.client.ApiException;
import org.openapitools.client.api.DefaultApi;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class Index {
	private final DefaultApi defaultApi;

	public @Autowired Index(DefaultApi defaultApi) {
		this.defaultApi = defaultApi;
	}

	@GetMapping("/")
	public String index(Model model) {
		model.addAttribute("user", "Usuario");
		try {
			model.addAttribute("templates", defaultApi.getAllTemplates());
		} catch (ApiException e) {
			throw new InternalErrorException(e.getMessage());
		}
		return "index.html";
	}
}
