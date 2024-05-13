package gal.udc.fic.prperez.pleste.client.view;

import gal.udc.fic.prperez.pleste.client.exceptions.InternalErrorException;
import gal.udc.fic.prperez.pleste.client.exceptions.ObjectNotFoundException;
import gal.udc.fic.prperez.pleste.client.exceptions.ObjectAlreadyExistsException;
import jakarta.servlet.http.HttpSession;
import org.openapitools.client.ApiException;
import org.openapitools.client.api.DefaultApi;
import org.openapitools.client.model.User;
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
public class NewEditUser {
	private final DefaultApi defaultApi;

	public @Autowired NewEditUser(DefaultApi defaultApi) {
		this.defaultApi = defaultApi;
	}

	@GetMapping("/newuser")
	public String newUser(Model model, HttpSession session) {
		CommonView.setModel(model, session);
		return getPage(model, null, null, null, false);
	}

	@GetMapping("/edituser")
	public String editUser(Model model, HttpSession session, @RequestParam(name = "id") String idParam) {
		CommonView.setModel(model, session);

		try {
			User user = defaultApi.getUser(idParam);
			return getPage(model, user.getUsername(), user.getEmail(), user.getRole().getValue(), true);
		} catch (ApiException e) {
			if(e.getCode() == HttpStatus.NOT_FOUND.value()) {
				throw new ObjectNotFoundException("usuario", idParam);
			} else {
				throw new InternalErrorException(e.getMessage());
			}
		}
	}

	private String getPage(Model model, String username, String email, String role, boolean edit) {
		if(username != null) {
			model.addAttribute("user_name", username);
		}
		if(email != null) {
			model.addAttribute("user_email", email);
		}
		if(role!= null) {
			model.addAttribute("user_role", role);
		}
		model.addAttribute("user_edit", edit);

		return "newuser.html";
	}

	@PostMapping(value = "/edituser", consumes = "application/x-www-form-urlencoded;charset=UTF-8")
	public ResponseEntity<String> editUser(@RequestParam(name = "id") String idParam,
	                                       @RequestParam(name = "user_name") String user_name,
	                                       @RequestParam(name = "user_email") String user_email,
	                                       @RequestParam(name = "user_password") String user_password,
	                                       @RequestParam(name = "user_role") String user_role) {
		try {
			User user = defaultApi.getUser(idParam)
					.username(user_name).email(user_email)
					.role(User.RoleEnum.valueOf(user_role));

			if(user_password != null && !user_password.isEmpty()) {
				user.setPassword(new BCryptPasswordEncoder().encode(user_password));
			}

			defaultApi.setUser(idParam, user);
		} catch (ApiException e) {
			if (e.getCode() == HttpStatus.NOT_FOUND.value()) {
				throw new ObjectNotFoundException("usuario", idParam);
			} else {
				throw new InternalErrorException(e.getMessage());
			}
		}

		return ResponseEntity
				.status(HttpStatus.SEE_OTHER)
				.header("Location","/user?id=" + idParam)
				.body("Modified user #" + idParam);
	}

	@PostMapping(value = "/newuser", consumes = "application/x-www-form-urlencoded;charset=UTF-8")
	public ResponseEntity<String> newUser(@RequestParam(name = "user_name") String user_name,
	                                      @RequestParam(name = "user_email") String user_email,
	                                      @RequestParam(name = "user_password") String user_password,
	                                      @RequestParam(name = "user_role") String user_role) {
		Long newId;

		try {
			try {
				defaultApi.getUserByName(user_name);
				throw new ObjectAlreadyExistsException("Usuario", user_name);
			} catch (ApiException e) {
				if (e.getCode() != HttpStatus.NOT_FOUND.value()) {
					throw e;
				}
			}

			User user = new User()
					.username(user_name)
					.email(user_email)
					.password(new BCryptPasswordEncoder().encode(user_password))
					.role(User.RoleEnum.fromValue(user_role));

			newId = defaultApi.addUser(user);

			return ResponseEntity
					.status(HttpStatus.SEE_OTHER)
					.header("Location","/user?id=" + newId)
					.body("Created user #" + newId);
		} catch (ApiException e) {
			if (e.getCode() == HttpStatus.NOT_FOUND.value()) {
				throw new ObjectNotFoundException("usuario", user_name);
			} else {
				throw new InternalErrorException(e.getMessage());
			}
		}

	}
}
