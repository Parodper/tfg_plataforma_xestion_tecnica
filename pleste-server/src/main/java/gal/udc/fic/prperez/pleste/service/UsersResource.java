package gal.udc.fic.prperez.pleste.service;

import gal.udc.fic.prperez.pleste.service.dao.SQLDaoFactoryUtil;
import gal.udc.fic.prperez.pleste.service.dao.users.*;
import gal.udc.fic.prperez.pleste.service.exceptions.authentication.UserAlreadyExistsException;
import gal.udc.fic.prperez.pleste.service.exceptions.authentication.UserNotFoundException;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.servers.Server;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Random;

@Path( "/users")
@Service
@Transactional
@OpenAPIDefinition(
		info = @Info(
				title = "pleste-service-components",
				version = "0.1.0"),
		servers = {
				@Server(
						url = "http://localhost:8080/api/v0")
		})
public class UsersResource {
	private final SQLUserDao userDatabase;
	private final SQLDaoFactoryUtil databaseFactory;

	public @Autowired UsersResource(SQLUserDao userDatabase, SQLDaoFactoryUtil databaseFactory) {
		this.userDatabase = userDatabase;
		this.databaseFactory = databaseFactory;
	}

	private String generateRandomToken() {
		Random rand = new Random();
		byte[] token = new byte[20];
		StringBuilder hexToken = new StringBuilder();

		rand.nextBytes(token);

		for(byte b : token) {
			hexToken.append(Integer.toHexString(b));
		}

		return hexToken.toString();
	}

	@POST
	@Produces({MediaType.APPLICATION_XML,MediaType.APPLICATION_JSON})
	@Consumes({MediaType.APPLICATION_XML,MediaType.APPLICATION_JSON})
	public Long addUser(User user) throws UserAlreadyExistsException {
		if(userDatabase.existsByUsername(user.getUsername())) {
			throw new UserAlreadyExistsException(user.getUsername());
		}

		return userDatabase.save(user).getId();
	}

	@Path("/login")
	@POST
	@Produces({MediaType.APPLICATION_XML,MediaType.APPLICATION_JSON})
	@Consumes({MediaType.APPLICATION_XML,MediaType.APPLICATION_JSON})
	public String login(@QueryParam("user") String username, String password) throws UserNotFoundException {
		//TODO: Arranxar isto
		String strippedPassword = password.replace("\"", "");
		if(userDatabase.existsByUsername(username)) {
			User user = userDatabase.getByUsername(username);
			if(new BCryptPasswordEncoder().matches(strippedPassword, user.getPassword())) {
				Token token = new Token();
				token.setUser(user);
				token.setToken(generateRandomToken());
				token = databaseFactory.getSqlTokenDao().save(token);
				return token.getToken();
			}
		}

		throw new UserNotFoundException(username);
	}

	@Path("/find")
	@GET
	@Produces({MediaType.APPLICATION_XML,MediaType.APPLICATION_JSON})
	public Long userByName(@QueryParam("name") String name) throws UserNotFoundException {
		if(userDatabase.existsByUsername(name)) {
			return userDatabase.getByUsername(name).getId();
		} else {
			throw new UserNotFoundException(name);
		}
	}

	@Path("/{userId: \\d+}")
	@GET
	@Produces({MediaType.APPLICATION_XML,MediaType.APPLICATION_JSON})
	public User getUser(@PathParam("userId") String idParam) throws UserNotFoundException {
		Long id = Long.parseLong(idParam);

		if(userDatabase.existsById(id)) {
			return userDatabase.getReferenceById(id);
		} else {
			throw new UserNotFoundException(idParam);
		}
	}

	@Path("/{userId: \\d+}/")
	@POST
	@Produces({MediaType.APPLICATION_XML,MediaType.APPLICATION_JSON})
	public User setUser(@PathParam("userId") String idParam, User user) throws UserNotFoundException {
		Long id = Long.parseLong(idParam);

		if(userDatabase.existsById(id)) {
			user.setId(id);
			return userDatabase.save(user);
		} else {
			throw new UserNotFoundException(idParam);
		}
	}

	@Path("/{userId: \\d+}")
	@DELETE
	@Produces({MediaType.APPLICATION_XML,MediaType.APPLICATION_JSON})
	public void deleteUser(@PathParam("userId") String idParam) throws UserNotFoundException {
		Long id = Long.parseLong(idParam);

		if(userDatabase.existsById(id)) {
			userDatabase.deleteById(id);
		} else {
			throw new UserNotFoundException(idParam);
		}
	}

	@Path("/{userId: \\d+}/role")
	@GET
	@Produces({MediaType.APPLICATION_XML,MediaType.APPLICATION_JSON})
	public Roles getUserRole(@PathParam("userId") String idParam) throws UserNotFoundException {
		Long id = Long.parseLong(idParam);

		if(userDatabase.existsById(id)) {
			return userDatabase.getReferenceById(id).getRole();
		} else {
			throw new UserNotFoundException(idParam);
		}
	}

	@Path("/{userId: \\d+}/role")
	@PUT
	@Produces({MediaType.APPLICATION_XML,MediaType.APPLICATION_JSON})
	public void setUserRole(@PathParam("userId") String idParam, Roles role) throws UserNotFoundException {
		Long id = Long.parseLong(idParam);

		if(userDatabase.existsById(id)) {
			User user = userDatabase.getReferenceById(id);
			user.setRole(role);
			userDatabase.save(user);
		} else {
			throw new UserNotFoundException(idParam);
		}
	}

	@Path("/{userId: \\d+}/logout")
	@POST
	@Consumes({MediaType.APPLICATION_XML,MediaType.APPLICATION_JSON})
	public void logout(@PathParam("userId") String idParam, String token) throws UserNotFoundException {
		Long id = Long.parseLong(idParam);
		String localToken = token.replace("\"", "");

		if(userDatabase.existsById(id) &&
				databaseFactory.getSqlTokenDao().existsByToken(localToken) &&
				databaseFactory.getSqlTokenDao().getByToken(localToken).getUser().getId().equals(id)) {
			databaseFactory.getSqlTokenDao().deleteByToken(token);
		} else {
			throw new UserNotFoundException(idParam);
		}
	}
}
