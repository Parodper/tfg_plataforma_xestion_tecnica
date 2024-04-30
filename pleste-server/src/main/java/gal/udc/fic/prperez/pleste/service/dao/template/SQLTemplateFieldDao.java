package gal.udc.fic.prperez.pleste.service.dao.template;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface SQLTemplateFieldDao extends JpaRepository<TemplateField, Long> {
	List<TemplateField> findByName(String name);
	boolean existsByName(String name);
	@Query(value = "SELECT DISTINCT template_id FROM template_field NATURAL JOIN template_fields WHERE template_field.name = ':name'", nativeQuery = true)
	List<Long> findByTemplateFieldName(@Param("name") String name);
}
