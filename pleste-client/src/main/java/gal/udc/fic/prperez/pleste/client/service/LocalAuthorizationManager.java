package gal.udc.fic.prperez.pleste.client.service;

import gal.udc.fic.prperez.pleste.client.exceptions.AccessDeniedException;
import org.openapitools.client.ApiException;
import org.openapitools.client.api.DefaultApi;
import org.openapitools.client.model.User.RoleEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.authorization.AuthorizationDecision;
import org.springframework.security.authorization.AuthorizationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.access.intercept.RequestAuthorizationContext;
import org.springframework.stereotype.Service;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.function.Supplier;

@Service
public class LocalAuthorizationManager implements AuthorizationManager<RequestAuthorizationContext> {
	private final DefaultApi defaultApi;

	public @Autowired LocalAuthorizationManager(DefaultApi defaultApi) {
		this.defaultApi = defaultApi;
	}

	@Override
	public void verify(Supplier<Authentication> authentication, RequestAuthorizationContext object) {
		if (authentication.get() instanceof AnonymousAuthenticationToken) {
			if(! (object.getRequest().getRequestURI().startsWith("/login") ||
					object.getRequest().getRequestURI().startsWith("/logout") ||
					object.getRequest().getRequestURI().startsWith("/css/") ||
					object.getRequest().getRequestURI().startsWith("/js/")) ) {
				throw new AccessDeniedException();
			}
		} else {
			try {
				LocalAuthentication user = (LocalAuthentication) authentication.get();

				String newId = defaultApi.getUserByName((String) user.getPrincipal()).toString();
				URL url = new URL(object.getRequest().getRequestURL().toString());
				if(RoleEnum.fromValue(defaultApi.getUserRole(newId)) == RoleEnum.NORMAL_USER && (
						url.getPath().matches("^/(new|edit)(template|user).*") ||
								(url.getPath().equals("/deleteelement") && url.getQuery().matches(".*type=(user|template).*")) )) {
					throw new AccessDeniedException();
				}
			} catch (ApiException | MalformedURLException e) {
				throw new AccessDeniedException();
			}
		}
	}

	@Override
	public AuthorizationDecision check(Supplier<Authentication> authentication, RequestAuthorizationContext object) {
		try {
			verify(authentication, object);
			return new AuthorizationDecision(true);
		} catch (AccessDeniedException e) {
			return new AuthorizationDecision(false);
		}
	}
}
