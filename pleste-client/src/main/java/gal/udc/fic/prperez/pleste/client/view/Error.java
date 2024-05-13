package gal.udc.fic.prperez.pleste.client.view;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class Error {
	@GetMapping(value = "errors")
	public ModelAndView error(@RequestParam(required = false) String message) {
		ModelAndView model = new ModelAndView("error.html");

		model.addObject("message", message);

		return model;
	}
}
