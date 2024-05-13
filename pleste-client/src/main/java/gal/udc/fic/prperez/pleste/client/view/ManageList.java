package gal.udc.fic.prperez.pleste.client.view;

import gal.udc.fic.prperez.pleste.client.exceptions.InternalErrorException;
import jakarta.servlet.http.HttpSession;
import org.openapitools.client.ApiException;
import org.openapitools.client.api.DefaultApi;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.Objects;

@Controller
public class ManageList {
	private final DefaultApi defaultApi;

	public @Autowired ManageList(DefaultApi defaultApi) {
		this.defaultApi = defaultApi;
	}

	@GetMapping("/manageusers")
	public String manageusers(Model model, HttpSession session) {
		CommonView.setModel(model, session);
		try {
			model.addAttribute("users", defaultApi.getAllUsers().stream().map(u -> {
				try {
					return defaultApi.getUser(u.toString());
				} catch (ApiException ignored) {
					return null;
				}
			}).filter(x -> !Objects.isNull(x)));
		} catch (ApiException e) {
			throw new InternalErrorException(e.getMessage());
		}
		return "manageusers.html";
	}

	@GetMapping("/managetemplates")
	public String managetemplates(Model model, HttpSession session) {
		CommonView.setModel(model, session);
		try {
			model.addAttribute("templates", defaultApi.getAllTemplates());
		} catch (ApiException e) {
			throw new InternalErrorException(e.getMessage());
		}
		return "managetemplates.html";
	}

	@GetMapping("/managecomponents")
	public String managecomponents(Model model, HttpSession session) {
		CommonView.setModel(model, session);
		try {
			model.addAttribute("components", defaultApi.getAllComponents());
		} catch (ApiException e) {
			throw new InternalErrorException(e.getMessage());
		}
		return "managecomponents.html";
	}
}