package gal.udc.fic.prperez.pleste.client.view;

import gal.udc.fic.prperez.pleste.client.exceptions.InternalErrorException;
import jakarta.servlet.http.HttpSession;
import org.openapitools.client.ApiException;
import org.openapitools.client.api.DefaultApi;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class Index {
	private final DefaultApi defaultApi;

	public @Autowired Index(DefaultApi defaultApi) {
		this.defaultApi = defaultApi;
	}

	@GetMapping("/")
	public String index(HttpSession session, Model model) {
		CommonView.setModel(model, session);
		try {
			model.addAttribute("templates", defaultApi.getAllTemplates(0, 10));
		} catch (ApiException e) {
			throw new InternalErrorException(e.getResponseBody());
		}
		return "index.html";
	}

	@PostMapping("/")
	public ResponseEntity<String> postIndex() {
		//Needed for some issue with redirecting from /login
		return ResponseEntity
				.status(HttpStatus.SEE_OTHER)
				.header("Location","/" ).build();
	}
}
