package gal.udc.fic.prperez.pleste.client.service;

import gal.udc.fic.prperez.pleste.client.exceptions.InternalErrorException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.openapitools.client.ApiException;
import org.openapitools.client.api.DefaultApi;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Component;

@Component
public class LocalLogoutHandler implements LogoutHandler {
	private final DefaultApi defaultApi;

	public @Autowired LocalLogoutHandler(DefaultApi defaultApi) {
		this.defaultApi = defaultApi;
	}

	@Override
	public void logout(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {
		HttpSession session = request.getSession();
		try {
			if(!(session.getAttribute("token") == null)) {
				defaultApi.logout(session.getAttribute("userid").toString(), (String) session.getAttribute("token"));
				session.removeAttribute("token");
				session.removeAttribute("userid");
				session.removeAttribute("username");
			}
		} catch (ApiException e) {
			if(! (e.getCode() == HttpStatus.NOT_FOUND.value())) {
				throw new InternalErrorException(e.getMessage());
			}
		}
	}
}
