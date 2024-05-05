package gal.udc.fic.prperez.pleste.client.view;

import gal.udc.fic.prperez.pleste.client.exceptions.AlreadyLoggedInException;
import gal.udc.fic.prperez.pleste.client.exceptions.InternalErrorException;
import jakarta.servlet.http.HttpSession;
import org.openapitools.client.ApiException;
import org.openapitools.client.api.DefaultApi;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
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

	@PostMapping("/login")
	public ResponseEntity<String> login(@RequestParam String username, @RequestParam String password, HttpSession session) {
		try {
			if(session.getAttribute("token") != null) {
				throw new AlreadyLoggedInException(username);
			} else {
				String token = defaultApi.login(username, new BCryptPasswordEncoder().encode(password));
				session.setAttribute("token", token);
				session.setAttribute("username", username);
				session.setAttribute("userid", defaultApi.userByName(username).getId());
				return ResponseEntity
						.status(HttpStatus.SEE_OTHER)
						.header("Location","/index")
						.body("Logged in successfully");
			}
		} catch (ApiException e) {
			if(e.getCode() == HttpStatus.BAD_REQUEST.value()) {
				throw new InternalErrorException("Wrong username or password");
			} else {
				throw new InternalErrorException(e.getMessage());
			}
		}
	}
}
