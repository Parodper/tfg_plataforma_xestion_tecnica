package gal.udc.fic.prperez.pleste.service;

import gal.udc.fic.prperez.pleste.service.dao.users.Roles;
import gal.udc.fic.prperez.pleste.service.dao.users.SQLTokenDao;
import gal.udc.fic.prperez.pleste.service.dao.users.SQLUserDao;
import gal.udc.fic.prperez.pleste.service.dao.users.User;
import gal.udc.fic.prperez.pleste.service.exceptions.authentication.IllegalActionForUserException;
import gal.udc.fic.prperez.pleste.service.exceptions.authentication.UserNotFoundException;
import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerRequestFilter;
import jakarta.ws.rs.container.PreMatching;
import jakarta.ws.rs.ext.Provider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Provider
@PreMatching
public class AuthenticationManager implements ContainerRequestFilter {
	private final SQLUserDao userDatabase;
	private final SQLTokenDao tokenDatabase;

	public @Autowired AuthenticationManager(SQLUserDao userDatabase, SQLTokenDao tokenDatabase) {
		this.userDatabase = userDatabase;
		this.tokenDatabase = tokenDatabase;
	}

	@Override
	public void filter(ContainerRequestContext requestContext) {
		String token, path, method;
		User user;

		token = requestContext.getHeaderString("X-API-KEY");
		path = requestContext.getUriInfo().getPath();
		method = requestContext.getMethod();

		if( token == null || token.isEmpty() ) {
			if(!path.matches("users/login") && !path.matches("users/search")) {
				throw new UserNotFoundException(token);
			}
		} else {
			if (tokenDatabase.existsByToken(token)) {
				user = tokenDatabase.getByToken(token).getUser();
				if (path.matches("templates/?.*")) {
					if(user.getRole().equals(Roles.ADMINISTRATOR) || (method.equals("GET") && user.getRole().equals(Roles.NORMAL_USER))) {
						return;
					} else {
						throw new IllegalActionForUserException(user.getUsername());
					}
				}
				if (path.matches("components/?.*")) {
					return;
				}
				if (path.matches("users/?.*")) {
					if( user.getRole().equals(Roles.NORMAL_USER) && method.equals("POST")) {
						throw new IllegalActionForUserException(user.getUsername());
					} else {
						return;
					}
				}
			}
			throw new UserNotFoundException(token);
		}
	}
}
