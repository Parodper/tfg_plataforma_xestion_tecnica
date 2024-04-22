package gal.udc.fic.prperez.pleste.client;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class Error {
	@GetMapping(value = "errors")
	public ModelAndView error(HttpServletRequest httpRequest) {
		ModelAndView model = new ModelAndView("error.html");

		model.addObject("code", httpRequest.getAttribute("jakarta.servlet.error.status_code"));
		model.addObject("message", httpRequest.getAttribute("jakarta.servlet.error.message"));

		return model;
	}
}
