package gal.udc.fic.prperez.pleste.service;

import gal.udc.fic.prperez.pleste.service.dao.SQLDaoFactoryUtil;
import gal.udc.fic.prperez.pleste.service.dao.template.FieldTypes;
import gal.udc.fic.prperez.pleste.service.dao.template.Template;
import gal.udc.fic.prperez.pleste.service.dao.template.TemplateField;
import gal.udc.fic.prperez.pleste.service.dao.users.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.ArrayList;

@Component
public class DataLoader implements ApplicationRunner {

	private final SQLUserDao userRepository;
	private final SQLDaoFactoryUtil sqlDaoFactoryUtil;

	@Autowired
	public DataLoader(SQLUserDao userRepository, SQLDaoFactoryUtil sqlDaoFactoryUtil) {
		this.userRepository = userRepository;
		this.sqlDaoFactoryUtil = sqlDaoFactoryUtil;
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
		TemplateResource templateResource = new TemplateResource(sqlDaoFactoryUtil.getSqlTemplateDao(), sqlDaoFactoryUtil, sqlDaoFactoryUtil.getSqlTemplateFieldDao());
		if(templateResource.getTemplatesByName("Ordenador").isEmpty()) {
			Template template = new Template();

			template.setName("Ordenador");
			template.setDescription("Ordenadores de la prueba");
			template.setFields(new ArrayList<>());
			template.getFields().add(new TemplateField(null, "Modelo", true, FieldTypes.TEXT));
			template.getFields().add(new TemplateField(null, "Data de compra", true, FieldTypes.DATETIME));
			template.getFields().add(new TemplateField(null, "Sistema operativo", true, FieldTypes.LINK));

			templateResource.addTemplate(template);
		}
		if(templateResource.getTemplatesByName("Licencia").isEmpty()) {
			Template template = new Template();

			template.setName("Licencia");
			template.setDescription("Licencias mercadas");
			template.setFields(new ArrayList<>());
			template.getFields().add(new TemplateField(null, "Programa", true, FieldTypes.TEXT));
			template.getFields().add(new TemplateField(null, "Data de compra", true, FieldTypes.DATETIME));
			template.getFields().add(new TemplateField(null, "Data limite", true, FieldTypes.DATETIME));

			templateResource.addTemplate(template);
		}
		if(templateResource.getTemplatesByName("Ordenador").isEmpty()) {
			Template template = new Template();

			template.setName("Ordenadores");
			template.setDescription("Ordenadores mercados");
			template.setFields(new ArrayList<>());
			template.getFields().add(new TemplateField(null, "Modelo", true, FieldTypes.TEXT));
			template.getFields().add(new TemplateField(null, "Data de compra", true, FieldTypes.DATETIME));
			template.getFields().add(new TemplateField(null, "Sistema operativo", true, FieldTypes.LINK));

			templateResource.addTemplate(template);
		}
	}
}