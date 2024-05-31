package gal.udc.fic.prperez.pleste.service;

import gal.udc.fic.prperez.pleste.service.dao.SQLDaoFactoryUtil;
import gal.udc.fic.prperez.pleste.service.dao.users.*;
import gal.udc.fic.prperez.pleste.service.exceptions.RESTExceptionSerializable;
import gal.udc.fic.prperez.pleste.service.exceptions.authentication.UserAlreadyExistsException;
import gal.udc.fic.prperez.pleste.service.exceptions.authentication.UserNotFoundException;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.servers.Server;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
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
						url = "http://" + Application.DOMAIN + Application.BASE_URL)
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

	@GET
	@Produces({MediaType.APPLICATION_JSON})
	@ApiResponses(value = {
			@ApiResponse(description = "Returns all users", responseCode = "200",
					content = @Content(array = @ArraySchema(schema = @Schema(implementation =  Long.class))))
	})
	public List<Long> getAllUsers() {
		return userDatabase.findAll().stream().map(User::getId).toList();
	}

	@POST
	@Produces({MediaType.APPLICATION_JSON})
	@Consumes({MediaType.APPLICATION_JSON})
	@ApiResponses(value = {
			@ApiResponse(description = "Returns created user ID", responseCode = "200",
					content = @Content(schema = @Schema(implementation = Long.class))),
			@ApiResponse(description = "A user with the same name already exists", responseCode = "409",
					content = @Content(schema = @Schema(implementation = RESTExceptionSerializable.class)))
	})
	public Long addUser(User user) throws UserAlreadyExistsException {
		if(userDatabase.existsByUsername(user.getUsername())) {
			throw new UserAlreadyExistsException(user.getUsername());
		}

		return userDatabase.save(user).getId();
	}

	@Path("/login")
	@POST
	@Produces({MediaType.APPLICATION_JSON})
	@Consumes({MediaType.APPLICATION_JSON})
	@ApiResponses(value = {
			@ApiResponse(description = "Returns a randomly generated token", responseCode = "200",
					content = @Content(schema = @Schema(implementation = JSONString.class))),
			@ApiResponse(description = "User not found, or password doesn't match", responseCode = "404",
					content = @Content(schema = @Schema(implementation = RESTExceptionSerializable.class)))
	})
	public JSONString login(@QueryParam("user") String username, JSONString passwordArg) throws UserNotFoundException {
		String password = passwordArg.toString();
		if(userDatabase.existsByUsername(username)) {
			User user = userDatabase.getByUsername(username);
			if(new BCryptPasswordEncoder().matches(password, user.getPassword())) {
				Token token = new Token();
				token.setUser(user);
				token.setToken(generateRandomToken());
				token = databaseFactory.getSqlTokenDao().save(token);
				return new JSONString(token.getToken());
			}
		}

		throw new UserNotFoundException(username);
	}

	@Path("/find")
	@GET
	@Produces({MediaType.APPLICATION_JSON})
	@ApiResponses(value = {
			@ApiResponse(description = "Returns the given user", responseCode = "200",
					content = @Content(schema = @Schema(implementation = Long.class))),
			@ApiResponse(description = "User not found", responseCode = "404",
					content = @Content(schema = @Schema(implementation = RESTExceptionSerializable.class)))
	})
	public Long getUserByName(@QueryParam("name") String name) throws UserNotFoundException {
		if(userDatabase.existsByUsername(name)) {
			return userDatabase.getByUsername(name).getId();
		} else {
			throw new UserNotFoundException(name);
		}
	}

	@Path("/{userId: \\d+}")
	@GET
	@Produces({MediaType.APPLICATION_JSON})
	@ApiResponses(value = {
			@ApiResponse(description = "Returns the given user", responseCode = "200",
					content = @Content(schema = @Schema(implementation = User.class))),
			@ApiResponse(description = "User not found", responseCode = "404",
					content = @Content(schema = @Schema(implementation = RESTExceptionSerializable.class)))
	})
	public User getUser(@PathParam("userId") String idParam) throws UserNotFoundException {
		Long id = Long.parseLong(idParam);

		Optional<User> user = userDatabase.findById(id);
		if(user.isPresent()) {
			return user.get();
		} else {
			throw new UserNotFoundException(idParam);
		}
	}

	@Path("/{userId: \\d+}/")
	@POST
	@Produces({MediaType.APPLICATION_JSON})
	@ApiResponses(value = {
			@ApiResponse(description = "Updates the user with the provided one", responseCode = "204"),
			@ApiResponse(description = "User not found", responseCode = "404",
					content = @Content(schema = @Schema(implementation = RESTExceptionSerializable.class)))
	})
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
	@Produces({MediaType.APPLICATION_JSON})
	@ApiResponses(value = {
			@ApiResponse(description = "Deletes the user", responseCode = "204"),
			@ApiResponse(description = "User not found", responseCode = "404",
					content = @Content(schema = @Schema(implementation = RESTExceptionSerializable.class)))
	})
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
	@Produces({MediaType.APPLICATION_JSON})
	@ApiResponses(value = {
			@ApiResponse(description = "Returns the role of the user", responseCode = "200",
					content = @Content(schema = @Schema(implementation = Roles.class))),
			@ApiResponse(description = "User not found", responseCode = "404",
					content = @Content(schema = @Schema(implementation = RESTExceptionSerializable.class)))
	})
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
	@Produces({MediaType.APPLICATION_JSON})
	@ApiResponses(value = {
			@ApiResponse(description = "Sets the role of the user", responseCode = "204"),
			@ApiResponse(description = "User not found", responseCode = "404",
					content = @Content(schema = @Schema(implementation = RESTExceptionSerializable.class)))
	})
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
	@Consumes({MediaType.APPLICATION_JSON})
	@ApiResponses(value = {
			@ApiResponse(description = "Removes the given token", responseCode = "204"),
			@ApiResponse(description = "User or token not found", responseCode = "404",
					content = @Content(schema = @Schema(implementation = RESTExceptionSerializable.class)))
	})
	public void logout(@PathParam("userId") String idParam, JSONString tokenArg) throws UserNotFoundException {
		Long id = Long.parseLong(idParam);
		String token = tokenArg.toString();

		if(userDatabase.existsById(id) &&
				databaseFactory.getSqlTokenDao().existsByToken(token) &&
				databaseFactory.getSqlTokenDao().getByToken(token).getUser().getId().equals(id)) {
			databaseFactory.getSqlTokenDao().deleteByToken(token);
		} else {
			throw new UserNotFoundException(idParam);
		}
	}
}
