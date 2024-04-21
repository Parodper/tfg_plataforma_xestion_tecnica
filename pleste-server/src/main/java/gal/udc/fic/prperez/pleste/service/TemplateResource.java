package gal.udc.fic.prperez.pleste.service;

import gal.udc.fic.prperez.pleste.service.dao.SQLDaoFactoryUtil;
import gal.udc.fic.prperez.pleste.service.dao.component.Component;
import gal.udc.fic.prperez.pleste.service.dao.component.Field;
import gal.udc.fic.prperez.pleste.service.dao.template.SQLTemplateDao;
import gal.udc.fic.prperez.pleste.service.dao.template.Template;
import gal.udc.fic.prperez.pleste.service.dao.template.TemplateField;
import gal.udc.fic.prperez.pleste.service.exceptions.TemplateFieldNotFoundException;
import gal.udc.fic.prperez.pleste.service.exceptions.template.TemplateAlreadyExistsException;
import gal.udc.fic.prperez.pleste.service.exceptions.template.TemplateNotFoundException;
import gal.udc.fic.prperez.pleste.service.exceptions.template.TemplateStillInUseException;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.servers.Server;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

@Path( "/templates")
@Service
@Transactional
@OpenAPIDefinition(
		info = @Info(
				title = "pleste-service-templates",
				version = "0.1.0"),
		servers = {
				@Server(
						url = "http://localhost:8080/api/v0")
		})
public class TemplateResource {
	private final SQLTemplateDao templateDatabase;
	private final SQLDaoFactoryUtil databaseFactory;

	private Template createBasicTemplate(Long id) {
		Template template = new Template();
		template.setId(id);
		return template;
	}

	private Template getTemplateUtil(Long id) throws TemplateNotFoundException {
		Optional<Template> template;
		try {
			template = templateDatabase.findById(id);
			if(template.isPresent()) {
				return template.get();
			} else {
				throw new NoSuchElementException();
			}
		} catch (NoSuchElementException e) {
			throw new TemplateNotFoundException(id.toString(), "");
		}
	}

	public @Autowired TemplateResource(SQLTemplateDao templateDatabase, SQLDaoFactoryUtil databaseFactory) {
		this.templateDatabase = templateDatabase;
		this.databaseFactory = databaseFactory;
	}

	@POST
	@Produces({MediaType.APPLICATION_XML,MediaType.APPLICATION_JSON})
	@Consumes({MediaType.APPLICATION_XML,MediaType.APPLICATION_JSON})
	public Long addTemplate(Template template) throws TemplateAlreadyExistsException {
		if(templateDatabase.findByName(template.getName()).isEmpty()) {
			return templateDatabase.save(template).getId();
		} else {
			throw new TemplateAlreadyExistsException(-1L, template.getName());
		}
	}

	@GET
	@Produces({MediaType.APPLICATION_XML,MediaType.APPLICATION_JSON})
	public List<Template> getAllTemplates() {
		return templateDatabase.findAll();
	}

	@Path("/find")
	@Produces({MediaType.APPLICATION_XML,MediaType.APPLICATION_JSON})
	public List<Template> getTemplatesByName(@QueryParam("name") String name) {
		return templateDatabase.findByName(name);
	}

	@Path("/{id : \\d}")
	@GET
	@Produces({MediaType.APPLICATION_XML,MediaType.APPLICATION_JSON})
	public Template getTemplate(@PathParam("id") String idPath) throws TemplateNotFoundException {
		Long id = Long.parseLong(idPath);
		return getTemplateUtil(id);
	}

	@Path("/{id : \\d}")
	@POST
	@Consumes({MediaType.APPLICATION_XML,MediaType.APPLICATION_JSON})
	public void modifyTemplate(@PathParam("id") String idPath, Template template) throws TemplateNotFoundException {
		Long id = Long.parseLong(idPath);
		if(templateDatabase.existsById(id)) {
			template.setId(id);
			templateDatabase.save(template);
		} else {
			throw new TemplateNotFoundException(idPath, template.getName());
		}
	}

	@Path("/{id : \\d}")
	@DELETE
	public void removeTemplate(@PathParam("id") String idPath) throws TemplateNotFoundException, TemplateStillInUseException {
		Long id = Long.parseLong(idPath);
		if(templateDatabase.existsById(id)) {
			if(databaseFactory.getSqlComponentDao().findByTemplate(createBasicTemplate(id)).isEmpty()) {
				templateDatabase.deleteById(id);
			} else {
				throw new TemplateStillInUseException(id, "");
			}
		} else {
			throw new TemplateNotFoundException(idPath, "");
		}
	}

	@Path("/{id : \\d}/components")
	@GET
	@Produces({MediaType.APPLICATION_XML,MediaType.APPLICATION_JSON})
	public List<Component> getTemplateComponents(@PathParam("id") String idPath) throws TemplateNotFoundException {
		Long id = Long.parseLong(idPath);
		return databaseFactory.getSqlComponentDao().findByTemplate(getTemplateUtil(id));
	}

	/// Fields

	@Path("/{id : \\d}/fields")
	@GET
	@Produces({MediaType.APPLICATION_XML,MediaType.APPLICATION_JSON})
	public List<TemplateField> getFieldsTemplate(@PathParam("id") String idPath) throws TemplateNotFoundException {
		Long id = Long.parseLong(idPath);
		return getTemplateUtil(id).getFields();
	}

	@Path("/{templateId : \\d}/fields/{fieldId : \\d}")
	@POST
	@Consumes({MediaType.APPLICATION_XML,MediaType.APPLICATION_JSON})
	public void modifyFieldTemplate(@PathParam("templateId") String idPath, @PathParam("fieldId") String fieldIdPath, TemplateField templateField) throws TemplateFieldNotFoundException {
		Long fieldId = Long.parseLong(fieldIdPath);
		if(databaseFactory.getSqlTemplateFieldDao().existsById(fieldId)) {
			templateField.setId(fieldId);
			databaseFactory.getSqlTemplateFieldDao().save(templateField);
		} else {
			throw new TemplateFieldNotFoundException(idPath, fieldIdPath);
		}
	}

	@Path("/{templateId : \\d}/fields/{fieldId : \\d}")
	@GET
	@Produces({MediaType.APPLICATION_XML,MediaType.APPLICATION_JSON})
	public TemplateField getFieldTemplate(@PathParam("templateId") String idPath, @PathParam("fieldId") String fieldIdPath) throws TemplateFieldNotFoundException {
		Long fieldId = Long.parseLong(fieldIdPath);
		if(databaseFactory.getSqlTemplateFieldDao().existsById(fieldId)) {
			return databaseFactory.getSqlTemplateFieldDao().getReferenceById(fieldId);
		} else {
			throw new TemplateFieldNotFoundException(idPath, fieldIdPath);
		}
	}

	@Path("/{templateId : \\d}/fields/{fieldId : \\d}")
	@DELETE
	public void removeTemplateField(@PathParam("templateId") String idPath, @PathParam("fieldId") String fieldIdPath) throws TemplateFieldNotFoundException {
		Long fieldId = Long.parseLong(fieldIdPath);
		if(databaseFactory.getSqlTemplateFieldDao().existsById(fieldId)) {
			TemplateField f = new TemplateField();
			f.setId(fieldId);
			for(Field field : databaseFactory.getSqlFieldDao().findByTemplateField(f)) {
				databaseFactory.getSqlFieldDao().delete(field);
			}
			databaseFactory.getSqlTemplateFieldDao().deleteById(fieldId);
		} else {
			throw new TemplateFieldNotFoundException(idPath, fieldIdPath);
		}
	}
}
