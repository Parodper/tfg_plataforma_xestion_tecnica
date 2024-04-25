package gal.udc.fic.prperez.pleste.service.dao.component;

import gal.udc.fic.prperez.pleste.service.dao.template.Template;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SQLComponentDao extends JpaRepository<Component, Long> {
	List<Component> findByTemplate(Template template);
	List<Component> findByName(String name);
}