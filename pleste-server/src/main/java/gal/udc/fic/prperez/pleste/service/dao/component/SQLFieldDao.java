package gal.udc.fic.prperez.pleste.service.dao.component;

import gal.udc.fic.prperez.pleste.service.dao.template.TemplateField;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SQLFieldDao extends JpaRepository<Field, Long> {
	public List<Field> findByTemplateField(TemplateField templateField);
}
