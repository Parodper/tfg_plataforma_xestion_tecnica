package gal.udc.fic.prperez.pleste.service.dao.template;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SQLTemplateDao extends JpaRepository<Template, Long> {
	List<Template> findByName(String name);
}