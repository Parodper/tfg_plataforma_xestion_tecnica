package gal.udc.fic.prperez.pleste.service;

import gal.udc.fic.prperez.pleste.service.dao.users.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class DataLoader implements ApplicationRunner {

	private final SQLUserDao userRepository;

	@Autowired
	public DataLoader(SQLUserDao userRepository) {
		this.userRepository = userRepository;
	}

	public void run(ApplicationArguments args) {
		if(!userRepository.existsByUsername("root")) {
			User u = new User();

			u.setUsername("root");
			u.setEmail("root@localhost");
			u.setPassword(new BCryptPasswordEncoder().encode("root"));

			u.setRole(Roles.ADMINISTRATOR);

			userRepository.save(u);
		}
	}
}