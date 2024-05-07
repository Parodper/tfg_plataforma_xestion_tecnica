package gal.udc.fic.prperez.pleste.client.view;

import org.openapitools.client.api.DefaultApi;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class Login {
	private final DefaultApi defaultApi;

	public @Autowired Login(DefaultApi defaultApi) {
		this.defaultApi = defaultApi;
	}

	@GetMapping("/login")
	public String login(@RequestParam(name = "error", required = false) String error, Model model) {
		if(error != null) {
			model.addAttribute("error", "Usuario ou contrasinal incorrecto");
		}
		return "login.html";
	}
}
