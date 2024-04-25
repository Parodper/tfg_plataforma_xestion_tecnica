package gal.udc.fic.prperez.pleste.service;

import gal.udc.fic.prperez.pleste.service.dao.SQLDaoFactoryUtil;
import gal.udc.fic.prperez.pleste.service.dao.component.Component;
import gal.udc.fic.prperez.pleste.service.dao.component.Field;
import gal.udc.fic.prperez.pleste.service.dao.component.SQLComponentDao;
import gal.udc.fic.prperez.pleste.service.dao.template.Template;
import gal.udc.fic.prperez.pleste.service.exceptions.ComponentFieldNotFoundException;
import gal.udc.fic.prperez.pleste.service.exceptions.ComponentIsMandatoryException;
import gal.udc.fic.prperez.pleste.service.exceptions.TemplateFieldNotFoundException;
import gal.udc.fic.prperez.pleste.service.exceptions.component.ComponentNotFoundException;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.servers.Server;
import jakarta.persistence.EntityNotFoundException;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

@Path( "/components")
@Service
@Transactional
@OpenAPIDefinition(
		info = @Info(
				title = "pleste-service-components",
				version = "0.1.0"),
		servers = {
				@Server(
						url = "http://localhost:8080/api/v0")
		})
public class ComponentResource {
	private final SQLComponentDao componentDatabase;
	private final SQLDaoFactoryUtil databaseFactory;

	private Component getComponentUtil(Long id) throws ComponentNotFoundException {
		Optional<Component> component;
		try {
			component = componentDatabase.findById(id);
			if(component.isPresent()) {
				return component.get();
			} else {
				throw new NoSuchElementException();
			}
		} catch (NoSuchElementException e) {
			throw new ComponentNotFoundException(id.toString(), "");
		}
	}

	public @Autowired ComponentResource(SQLComponentDao componentDatabase, SQLDaoFactoryUtil databaseFactory) {
		this.componentDatabase = componentDatabase;
		this.databaseFactory = databaseFactory;
	}

	@POST
	@Produces({MediaType.APPLICATION_XML,MediaType.APPLICATION_JSON})
	@Consumes({MediaType.APPLICATION_XML,MediaType.APPLICATION_JSON})
	public Long addComponent(Component component) throws ComponentNotFoundException {
		return componentDatabase.save(component).getId();
	}

	@GET
	@Produces({MediaType.APPLICATION_XML,MediaType.APPLICATION_JSON})
	public List<Component> getAllComponents() {
		return componentDatabase.findAll();
	}

	@Path("/find")
	@Produces({MediaType.APPLICATION_XML,MediaType.APPLICATION_JSON})
	public List<Component> getComponentsByName(@QueryParam("name") String name) {
		return componentDatabase.findByName(name);
	}

	@Path("/{id : \\d}")
	@GET
	@Produces({MediaType.APPLICATION_XML,MediaType.APPLICATION_JSON})
	public Component getComponent(@PathParam("id") String idPath) throws ComponentNotFoundException {
		Long id = Long.parseLong(idPath);
		return getComponentUtil(id);
	}

	@Path("/{id : \\d}")
	@POST
	@Consumes({MediaType.APPLICATION_XML,MediaType.APPLICATION_JSON})
	public void modifyComponent(@PathParam("id") String idPath, Component component) throws ComponentNotFoundException {
		Long id = Long.parseLong(idPath);
		if(componentDatabase.existsById(id)) {
			component.setId(id);
			componentDatabase.save(component);
		} else {
			throw new ComponentNotFoundException(idPath, component.getName());
		}
	}

	@Path("/{id : \\d}")
	@DELETE
	public void removeComponent(@PathParam("id") String idPath) throws ComponentNotFoundException {
		Long id = Long.parseLong(idPath);
		if(componentDatabase.existsById(id)) {
			componentDatabase.deleteById(id);
		} else {
			throw new ComponentNotFoundException(idPath, "");
		}
	}

	@Path("/{id : \\d}/template")
	@GET
	@Produces({MediaType.APPLICATION_XML,MediaType.APPLICATION_JSON})
	public Template getParentTemplate(@PathParam("id") String idPath) throws ComponentNotFoundException {
		Long id = Long.parseLong(idPath);
		return componentDatabase.getReferenceById(id).getTemplate();
	}

	/// Fields

	@Path("/{id : \\d}/fields")
	@GET
	@Produces({MediaType.APPLICATION_XML,MediaType.APPLICATION_JSON})
	public List<Field> getFieldsComponent(@PathParam("id") String idPath) throws ComponentNotFoundException {
		Long id = Long.parseLong(idPath);
		return getComponentUtil(id).getFields();
	}

	@Path("/{componentId : \\d}/fields/{fieldId : \\d}")
	@POST
	@Consumes({MediaType.APPLICATION_XML,MediaType.APPLICATION_JSON})
	public void modifyFieldComponent(@PathParam("componentId") String idPath, @PathParam("fieldId") String fieldIdPath, Field componentField) throws ComponentFieldNotFoundException {
		Long fieldId = Long.parseLong(fieldIdPath);
		if(databaseFactory.getSqlFieldDao().existsById(fieldId)) {
			componentField.setId(fieldId);
			databaseFactory.getSqlFieldDao().save(componentField);
		} else {
			throw new ComponentFieldNotFoundException(idPath, fieldIdPath);
		}
	}

	@Path("/{componentId : \\d}/fields/{fieldId : \\d}")
	@GET
	@Produces({MediaType.APPLICATION_XML,MediaType.APPLICATION_JSON})
	public Field getFieldComponent(@PathParam("componentId") String idPath, @PathParam("fieldId") String fieldIdPath) throws ComponentFieldNotFoundException {
		Long fieldId = Long.parseLong(fieldIdPath);
		if(databaseFactory.getSqlFieldDao().existsById(fieldId)) {
			return databaseFactory.getSqlFieldDao().getReferenceById(fieldId);
		} else {
			throw new ComponentFieldNotFoundException(idPath, fieldIdPath);
		}
	}

	@Path("/{componentId : \\d}/fields/{fieldId : \\d}")
	@DELETE
	public void removeComponentField(@PathParam("componentId") String idPath, @PathParam("fieldId") String fieldIdPath) throws TemplateFieldNotFoundException {
		Long fieldId = Long.parseLong(fieldIdPath);

		try {
			Field field = databaseFactory.getSqlFieldDao().getReferenceById(fieldId);

			if(field.getTemplateField().isMandatory()) {
				throw new ComponentIsMandatoryException(idPath);
			} else {
				field.setContent(null);
				databaseFactory.getSqlFieldDao().save(field);
			}
		} catch (EntityNotFoundException e) {
			throw new TemplateFieldNotFoundException(idPath, fieldIdPath);
		}
	}
}
