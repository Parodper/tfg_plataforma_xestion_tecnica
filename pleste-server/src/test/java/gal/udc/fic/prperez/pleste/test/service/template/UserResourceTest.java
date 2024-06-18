package gal.udc.fic.prperez.pleste.test.service.template;

import gal.udc.fic.prperez.pleste.service.Application;
import gal.udc.fic.prperez.pleste.service.UsersResource;
import gal.udc.fic.prperez.pleste.service.dao.users.User;
import gal.udc.fic.prperez.pleste.service.exceptions.authentication.UserAlreadyExistsException;
import gal.udc.fic.prperez.pleste.service.exceptions.authentication.UserNotFoundException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest(classes = Application.class)
public class UserResourceTest {
	@Autowired
	UsersResource usersResource;
	
	private static User createTestUser() {
		User user = new User();
		user.setUsername("A");
		
		return user;
	}

	@Test
	public void createUserTest() {
		Long id;

		id = usersResource.addUser(createTestUser());

		assertNotNull(id);
		assertThrows(UserAlreadyExistsException.class, () -> usersResource.addUser(createTestUser()));

		usersResource.deleteUser(id.toString());
	}

	@Test
	public void modifyUserTest() {
		Long id;

		id = usersResource.addUser(createTestUser());

		User u = usersResource.getUser(id.toString());
		assertEquals(u.getUsername(), createTestUser().getUsername());
		u.setUsername("B");
		usersResource.setUser(id.toString(), u);
		u = usersResource.getUser(id.toString());
		assertEquals("B", u.getUsername());

		usersResource.deleteUser(id.toString());
	}

	@Test
	public void deleteUserTest() {
		final Long id = usersResource.addUser(createTestUser());
		assertEquals(createTestUser().getUsername(), usersResource.getUser(id.toString()).getUsername());
		usersResource.deleteUser(id.toString());
		assertThrows(UserNotFoundException.class, () -> usersResource.getUser(id.toString()));
	}
}
