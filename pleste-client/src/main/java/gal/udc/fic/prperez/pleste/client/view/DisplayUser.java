package gal.udc.fic.prperez.pleste.client.view;

import gal.udc.fic.prperez.pleste.client.exceptions.InternalErrorException;
import gal.udc.fic.prperez.pleste.client.exceptions.ObjectNotFoundException;
import jakarta.servlet.http.HttpSession;
import org.openapitools.client.ApiException;
import org.openapitools.client.api.DefaultApi;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class DisplayUser {
	private final DefaultApi defaultApi;

	public DisplayUser(DefaultApi defaultApi) {
		this.defaultApi = defaultApi;
	}

	@GetMapping("/user")
	public String displayTemplate(@RequestParam(name = "id") String idPath, Model model, HttpSession session) {
		CommonView.setModel(model, session);

		try {
			model.addAttribute("user", defaultApi.getUser(idPath));
		} catch (ApiException e) {
			if (e.getCode() == HttpStatus.NOT_FOUND.value()) {
				throw new ObjectNotFoundException("usuario", idPath);
			} else {
				throw new InternalErrorException(e.getMessage());
			}
		}

		return "displayuser.html";
	}
}
