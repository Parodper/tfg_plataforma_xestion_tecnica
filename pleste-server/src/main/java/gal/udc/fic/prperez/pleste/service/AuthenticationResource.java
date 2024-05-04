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
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.util.Random;
import java.util.random.RandomGenerator;

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
public class AuthenticationResource {
	private final SQLUserDao userDatabase;
	private final SQLPasswordDao passwordDatabase;
	private final SQLDaoFactoryUtil databaseFactory;

	public @Autowired AuthenticationResource(SQLUserDao userDatabase, SQLPasswordDao passwordDatabase, SQLDaoFactoryUtil databaseFactory) {
		this.userDatabase = userDatabase;
		this.passwordDatabase = passwordDatabase;
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
	public Long addUser(String username, String email, String password ) {
		User user = new User();
		Password pass = new Password();

		if(email != null) {
			user.setEmail(email);
		}
		user.setUsername(username);
		pass.setPassword(password);
		user.setPassword(pass);
		user.setRole(Roles.NORMAL_USER);

		if(userDatabase.existsByUsername(username)) {
			throw new UserAlreadyExistsException(username);
		}

		return userDatabase.save(user).getId();
	}

	@Path("/login")
	@POST
	@Produces({MediaType.APPLICATION_XML,MediaType.APPLICATION_JSON})
	@Consumes({MediaType.APPLICATION_XML,MediaType.APPLICATION_JSON})
	public String login(String username, String password ) {
		if(userDatabase.existsByUsername(username)) {
			User user = userDatabase.getByUsername(username);
			if(user.getPassword().getPassword().equals(password)) {
				Token token = new Token();
				token.setUser(user);
				token.setToken(generateRandomToken());
				databaseFactory.getSqlTokenDao().save(token);
				return token.getToken();
			}
		}

		throw new UserNotFoundException(username);
	}

	@Path("/{userId: \\d+}")
	@GET
	@Produces({MediaType.APPLICATION_XML,MediaType.APPLICATION_JSON})
	public User getUser(@PathParam("userId") String idParam) {
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
	public User setUser(@PathParam("userId") String idParam, User user) {
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
	public void deleteUser(@PathParam("userId") String idParam) {
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
	public Roles getUserRole(@PathParam("userId") String idParam) {
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
	public void setUserRole(@PathParam("userId") String idParam, Roles role) {
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
	@PUT
	@Produces({MediaType.APPLICATION_XML,MediaType.APPLICATION_JSON})
	public void logout(@PathParam("userId") String idParam, String token) {
		Long id = Long.parseLong(idParam);

		if(userDatabase.existsById(id) && databaseFactory.getSqlTokenDao().existsByToken(token)) {
			databaseFactory.getSqlTokenDao().deleteByToken(token);
		} else {
			throw new UserNotFoundException(idParam);
		}
	}
}
