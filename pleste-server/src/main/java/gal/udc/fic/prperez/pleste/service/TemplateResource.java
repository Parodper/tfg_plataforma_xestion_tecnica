package gal.udc.fic.prperez.pleste.service;

import gal.udc.fic.prperez.pleste.service.dao.SQLDaoFactoryUtil;
import gal.udc.fic.prperez.pleste.service.dao.component.Component;
import gal.udc.fic.prperez.pleste.service.dao.component.Field;
import gal.udc.fic.prperez.pleste.service.dao.template.SQLTemplateDao;
import gal.udc.fic.prperez.pleste.service.dao.template.SQLTemplateFieldDao;
import gal.udc.fic.prperez.pleste.service.dao.template.Template;
import gal.udc.fic.prperez.pleste.service.dao.template.TemplateField;
import gal.udc.fic.prperez.pleste.service.exceptions.TemplateFieldAlreadyExistsException;
import gal.udc.fic.prperez.pleste.service.exceptions.TemplateFieldNotFoundException;
import gal.udc.fic.prperez.pleste.service.exceptions.template.TemplateAlreadyExistsException;
import gal.udc.fic.prperez.pleste.service.exceptions.template.TemplateNotFoundException;
import gal.udc.fic.prperez.pleste.service.exceptions.template.TemplateStillInUseException;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.servers.Server;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

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
	private final SQLTemplateFieldDao templateFieldDatabase;
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

	public @Autowired TemplateResource(SQLTemplateDao templateDatabase, SQLDaoFactoryUtil databaseFactory, SQLTemplateFieldDao templateFieldDatabase) {
		this.templateDatabase = templateDatabase;
		this.databaseFactory = databaseFactory;
		this.templateFieldDatabase = templateFieldDatabase;
	}

	@POST
	@Produces({MediaType.APPLICATION_XML,MediaType.APPLICATION_JSON})
	@Consumes({MediaType.APPLICATION_XML,MediaType.APPLICATION_JSON})
	@ApiResponses(value = {
			@ApiResponse(description = "Returns created template ID", responseCode = "200",
					content = @Content(schema = @Schema(implementation = Long.class))),
			@ApiResponse(description = "A template with the same name already exists", responseCode = "409",
					content = @Content(schema = @Schema(implementation = String.class)))
	})
	public Long addTemplate(Template template) throws TemplateAlreadyExistsException {
		if(templateDatabase.findByName(template.getName()).isEmpty()) {
			return templateDatabase.save(template).getId();
		} else {
			throw new TemplateAlreadyExistsException(-1L, template.getName());
		}
	}

	@GET
	@Produces({MediaType.APPLICATION_XML,MediaType.APPLICATION_JSON})
	@ApiResponses(value = {
			@ApiResponse(description = "Returns all templates", responseCode = "200",
					content = @Content(array = @ArraySchema(schema = @Schema(implementation = Template.class))))
	})
	public List<Template> getAllTemplates() {
		return templateDatabase.findAll();
	}

	@Path("/find")
	@Produces({MediaType.APPLICATION_XML,MediaType.APPLICATION_JSON})
	@ApiResponses(value = {
			@ApiResponse(description = "Returns all matching templates", responseCode = "200",
					content = @Content(array = @ArraySchema(schema = @Schema(implementation = Template.class))))
	})
	public List<Template> getTemplatesByName(@QueryParam("name") String name) {
		return templateDatabase.findByName(name);
	}

	@Path("/{id : \\d+}")
	@GET
	@Produces({MediaType.APPLICATION_XML,MediaType.APPLICATION_JSON})
	@ApiResponses(value = {
			@ApiResponse(description = "Returns the given template", responseCode = "200",
					content = @Content(schema = @Schema(implementation = Template.class))),
			@ApiResponse(description = "Template not found", responseCode = "404",
					content = @Content(schema = @Schema(implementation = String.class)))
	})
	public Template getTemplate(@PathParam("id") String idPath) throws TemplateNotFoundException {
		Long id = Long.parseLong(idPath);
		return getTemplateUtil(id);
	}

	@Path("/{id : \\d+}")
	@POST
	@Consumes({MediaType.APPLICATION_XML,MediaType.APPLICATION_JSON})
	@ApiResponses(value = {
			@ApiResponse(description = "Updates the template with the provided one", responseCode = "204"),
			@ApiResponse(description = "Template not found", responseCode = "404",
					content = @Content(schema = @Schema(implementation = String.class)))
	})
	public void modifyTemplate(@PathParam("id") String idPath, Template template) throws TemplateNotFoundException {
		Long id = Long.parseLong(idPath);
		if(templateDatabase.existsById(id)) {
			template.setId(id);
			templateDatabase.save(template);
		} else {
			throw new TemplateNotFoundException(idPath, template.getName());
		}
	}

	@Path("/{id : \\d+}")
	@DELETE
	@ApiResponses(value = {
			@ApiResponse(description = "Deletes the template", responseCode = "204"),
			@ApiResponse(description = "Template not found", responseCode = "404",
					content = @Content(schema = @Schema(implementation = String.class))),
			@ApiResponse(description = "Template still has components", responseCode = "400",
					content = @Content(schema = @Schema(implementation = String.class)))
	})
	public void removeTemplate(@PathParam("id") String idPath) throws TemplateNotFoundException, TemplateStillInUseException {
		Long id = Long.parseLong(idPath);
		if(templateDatabase.existsById(id)) {
			if(databaseFactory.getSqlComponentDao().findByTemplate(createBasicTemplate(id)).isEmpty()) {
				templateDatabase.deleteById(id);
			} else {
				throw new TemplateStillInUseException(idPath, "");
			}
		} else {
			throw new TemplateNotFoundException(idPath, "");
		}
	}

	@Path("/{id : \\d+}/components")
	@GET
	@Produces({MediaType.APPLICATION_XML,MediaType.APPLICATION_JSON})
	@ApiResponses(value = {
			@ApiResponse(description = "Returns all components created from this template", responseCode = "200",
					content = @Content(array = @ArraySchema(schema = @Schema(implementation = Component.class)))),
			@ApiResponse(description = "Template not found", responseCode = "404",
					content = @Content(schema = @Schema(implementation = String.class)))
	})
	public List<Component> getTemplateComponents(@PathParam("id") String idPath) throws TemplateNotFoundException {
		Long id = Long.parseLong(idPath);
		return databaseFactory.getSqlComponentDao().findByTemplate(getTemplateUtil(id));
	}

	/// Fields

	private boolean templateDoesntContainsField(String templateIdS, String fieldIdS) {
		Long templateId = Long.parseLong(templateIdS);
		Long fieldId = Long.parseLong(fieldIdS);

		return templateDatabase.existsById(templateId) &&
				templateFieldDatabase.existsById(fieldId) &&
				templateDatabase.getReferenceById(templateId).getFields().stream()
						.anyMatch(f -> f.getId().equals(fieldId));
	}

	@Path("/{id : \\d+}/fields")
	@GET
	@Produces({MediaType.APPLICATION_XML,MediaType.APPLICATION_JSON})
	@ApiResponses(value = {
			@ApiResponse(description = "Returns the fields of the template", responseCode = "200",
					content = @Content(array = @ArraySchema(schema = @Schema(implementation = TemplateField.class)))),
			@ApiResponse(description = "Template not found", responseCode = "404",
					content = @Content(schema = @Schema(implementation = String.class)))
	})
	public List<TemplateField> getFieldsTemplate(@PathParam("id") String idPath) throws TemplateNotFoundException {
		Long id = Long.parseLong(idPath);
		return getTemplateUtil(id).getFields();
	}

	@Path("/{id : \\d+}/fields")
	@POST
	@Produces({MediaType.APPLICATION_XML,MediaType.APPLICATION_JSON})
	@Consumes({MediaType.APPLICATION_XML,MediaType.APPLICATION_JSON})
	@ApiResponses(value = {
			@ApiResponse(description = "Returns the ID of the template field", responseCode = "200",
					content = @Content(schema = @Schema(implementation = Long.class))),
			@ApiResponse(description = "Template not found", responseCode = "404",
					content = @Content(schema = @Schema(implementation = String.class))),
			@ApiResponse(description = "Template has at least one Component", responseCode = "400",
					content = @Content(schema = @Schema(implementation = String.class))),
			@ApiResponse(description = "A field with the same name already exists", responseCode = "409",
					content = @Content(schema = @Schema(implementation = String.class)))
	})
	public Long addFieldTemplate(@PathParam("id") String idPath, TemplateField templateField) throws TemplateNotFoundException, TemplateStillInUseException, TemplateFieldAlreadyExistsException {
		Long id = Long.parseLong(idPath);

		if(!templateDatabase.existsById(id)) {
			throw new TemplateNotFoundException(idPath, "");
		}
		if(templateFieldDatabase.existsByName(templateField.getName()) &&
				templateFieldDatabase.findByTemplateFieldName(templateField.getName()).contains(id)) {
			throw new TemplateFieldAlreadyExistsException(idPath, templateField.getName());
		}

		if(!databaseFactory.getSqlComponentDao().findByTemplate(new Template(id)).isEmpty()) {
			throw new TemplateStillInUseException(idPath, templateField.getName());
		}

		Template t = templateDatabase.getReferenceById(id);
		t.getFields().add(templateField);
		templateDatabase.save(t);

		return templateFieldDatabase.save(templateField).getId();
	}

	@Path("/{templateId : \\d+}/fields/{fieldId : \\d+}")
	@POST
	@Consumes({MediaType.APPLICATION_XML,MediaType.APPLICATION_JSON})
	@ApiResponses(value = {
			@ApiResponse(description = "Modifies the given field", responseCode = "204"),
			@ApiResponse(description = "Template, or the field, not found", responseCode = "404",
					content = @Content(schema = @Schema(implementation = String.class))),
			@ApiResponse(description = "Template has at least one Component", responseCode = "400",
					content = @Content(schema = @Schema(implementation = String.class))),
	})
	public void modifyFieldTemplate(@PathParam("templateId") String idPath, @PathParam("fieldId") String fieldIdPath, TemplateField templateField) throws TemplateFieldNotFoundException, TemplateStillInUseException {
		Long fieldId = Long.parseLong(fieldIdPath);
		if(templateFieldDatabase.existsById(fieldId)) {
			if(templateDoesntContainsField(idPath, fieldIdPath)) {
				throw new TemplateFieldNotFoundException(idPath, fieldIdPath);
			}

			if(databaseFactory.getSqlComponentDao().findByTemplate(new Template(Long.parseLong(idPath))).isEmpty()) {
				templateField.setId(fieldId);
				templateFieldDatabase.save(templateField);
			} else {
				throw new TemplateStillInUseException(idPath, "");
			}
		} else {
			throw new TemplateFieldNotFoundException(idPath, fieldIdPath);
		}
	}

	@Path("/{templateId : \\d+}/fields/{fieldId : \\d+}")
	@GET
	@Produces({MediaType.APPLICATION_XML,MediaType.APPLICATION_JSON})
	@ApiResponses(value = {
			@ApiResponse(description = "Returns the given field", responseCode = "200",
					content = @Content(schema = @Schema(implementation = TemplateField.class))),
			@ApiResponse(description = "Template, or the field, not found", responseCode = "404",
					content = @Content(schema = @Schema(implementation = String.class)))
	})
	public TemplateField getFieldTemplate(@PathParam("templateId") String idPath, @PathParam("fieldId") String fieldIdPath) throws TemplateFieldNotFoundException {
		Long fieldId = Long.parseLong(fieldIdPath);
		if(templateDoesntContainsField(idPath, fieldIdPath)) {
			return templateFieldDatabase.getReferenceById(fieldId);
		} else {
			throw new TemplateFieldNotFoundException(idPath, fieldIdPath);
		}
	}

	@Path("/{templateId : \\d+}/fields/{fieldId : \\d+}")
	@DELETE

	@ApiResponses(value = {
			@ApiResponse(description = "Deletes the given field, also from all its components", responseCode = "204"),
			@ApiResponse(description = "Template, or the field, not found", responseCode = "404",
					content = @Content(schema = @Schema(implementation = String.class)))
	})
	public void removeTemplateField(@PathParam("templateId") String idPath, @PathParam("fieldId") String fieldIdPath) throws TemplateFieldNotFoundException {
		Long fieldId = Long.parseLong(fieldIdPath);
		if(templateFieldDatabase.existsById(fieldId)) {
			if(templateDoesntContainsField(idPath, fieldIdPath)) {
				throw new TemplateFieldNotFoundException(idPath, fieldIdPath);
			}

			TemplateField f = new TemplateField();
			f.setId(fieldId);
			for(Field field : databaseFactory.getSqlFieldDao().findByTemplateField(f)) {
				databaseFactory.getSqlFieldDao().delete(field);
			}

			Template t = templateDatabase.getReferenceById(Long.parseLong(idPath));
			t.getFields().removeIf(templateField -> templateField.getId().equals(fieldId));
			templateDatabase.save(t);
			templateFieldDatabase.deleteById(fieldId);
		} else {
			throw new TemplateFieldNotFoundException(idPath, fieldIdPath);
		}
	}
}
