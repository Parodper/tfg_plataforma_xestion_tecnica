package gal.udc.fic.prperez.pleste.client;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class NewTemplate {
	@GetMapping("/newtemplate")
	public String newTemplate(Model model) {
		return "newtemplate.html";
	}
}
