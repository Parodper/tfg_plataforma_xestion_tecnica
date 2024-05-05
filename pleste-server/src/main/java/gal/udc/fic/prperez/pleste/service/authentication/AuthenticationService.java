package gal.udc.fic.prperez.pleste.service.authentication;

import gal.udc.fic.prperez.pleste.service.Application;
import gal.udc.fic.prperez.pleste.service.dao.users.Roles;
import gal.udc.fic.prperez.pleste.service.dao.users.SQLTokenDao;
import gal.udc.fic.prperez.pleste.service.dao.users.User;
import gal.udc.fic.prperez.pleste.service.exceptions.authentication.UserNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.stereotype.Service;
import org.springframework.security.core.Authentication;

@Service
public class AuthenticationService {
	private @Autowired SQLTokenDao sqlTokenDao;

	public @Autowired AuthenticationService(SQLTokenDao sqlTokenDao) {
		this.sqlTokenDao = sqlTokenDao;
	}

	private static final String AUTH_TOKEN_HEADER_NAME = "X-API-KEY";

	public Authentication getAuthentication(HttpServletRequest request) {
		String apiKey = request.getHeader(AUTH_TOKEN_HEADER_NAME);
		//TODO: Fix this!!!
		for(String match : new String[]{"/openapi.json", Application.BASE_URL + "/users/login", "/error"}) {
			if(request.getRequestURI().matches(match)) {
				return new AnonymousAuthenticationToken("anonymous", "anonymous", AuthorityUtils.createAuthorityList("ROLE_ANONYMOUS"));
			}
		}
		if(apiKey != null && sqlTokenDao.existsByToken(apiKey) &&
				canAccess(request.getMethod(), request.getRequestURI(), sqlTokenDao.getByToken(apiKey).getUser())) {
			return new ApiKeyAuthentication(apiKey, AuthorityUtils.NO_AUTHORITIES);
		} else {
			throw new UserNotFoundException("API_KEY");
		}
	}

	private boolean canAccess(String method, String url, User user) {
		if(url.matches("^/templates.*")) {
			return user.getRole().equals(Roles.ADMINISTRATOR) || (user.getRole().equals(Roles.NORMAL_USER) && method.equals("GET"));
		} else if (url.matches("^/components.*")) {
			return user.getRole().equals(Roles.ADMINISTRATOR) || user.getRole().equals(Roles.NORMAL_USER);
		} else {
			return true;
		}
	}
}
