package gal.udc.fic.prperez.pleste.client.view;

import gal.udc.fic.prperez.pleste.client.exceptions.ObjectNotFoundException;
import jakarta.servlet.http.HttpSession;
import org.openapitools.client.ApiException;
import org.openapitools.client.api.DefaultApi;
import org.openapitools.client.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class Logout {
	private final DefaultApi defaultApi;

	public @Autowired Logout(DefaultApi defaultApi) {
		this.defaultApi = defaultApi;
	}

	@GetMapping("/logout")
	@PostMapping("/logout")
	public ResponseEntity<String> logout(HttpSession session) {
		try {
			if(session.getAttribute("token") == null) {
				throw new ObjectNotFoundException("User not logged in");
			} else {
				defaultApi.logout((String) session.getAttribute("userid"), (String) session.getAttribute("token"));
				session.removeAttribute("token");
				session.removeAttribute("userid");
				session.removeAttribute("username");
			}
		} catch (ApiException e) {
			throw new RuntimeException(e);
		}

		return ResponseEntity
				.status(HttpStatus.SEE_OTHER)
				.header("Location","/login")
				.body("Logged out successfully");
	}
}
