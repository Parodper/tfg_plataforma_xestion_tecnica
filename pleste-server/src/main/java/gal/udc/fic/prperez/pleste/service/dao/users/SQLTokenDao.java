package gal.udc.fic.prperez.pleste.service.dao.users;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SQLTokenDao extends JpaRepository<Token, Long> {
	List<Token> findByUser(User user);
	boolean existsByToken(String token);
	Token getByToken(String token);
	void deleteByToken(String token);
}
