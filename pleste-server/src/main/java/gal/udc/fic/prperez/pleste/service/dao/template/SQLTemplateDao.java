package gal.udc.fic.prperez.pleste.service.dao.template;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SQLTemplateDao extends JpaRepository<Template, Long> {}