package gal.udc.fic.prperez.pleste.service.dao.users;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SQLUserDao extends JpaRepository<User, Long> {
	User getByUsername(String username);
	boolean existsByUsername(String username);
	List<User> findAll();
}
