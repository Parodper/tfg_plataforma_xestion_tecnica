package gal.udc.fic.prperez.pleste.service;

import gal.udc.fic.prperez.pleste.service.dao.SQLDaoFactoryUtil;
import gal.udc.fic.prperez.pleste.service.dao.component.Component;
import gal.udc.fic.prperez.pleste.service.dao.component.Field;
import gal.udc.fic.prperez.pleste.service.dao.component.SQLComponentDao;
import gal.udc.fic.prperez.pleste.service.dao.template.FieldTypes;
import gal.udc.fic.prperez.pleste.service.dao.template.Template;
import gal.udc.fic.prperez.pleste.service.dao.template.TemplateField;
import gal.udc.fic.prperez.pleste.service.exceptions.component.*;
import gal.udc.fic.prperez.pleste.service.exceptions.TemplateFieldNotFoundException;
import gal.udc.fic.prperez.pleste.service.exceptions.template.TemplateNotFoundException;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.servers.Server;
import jakarta.persistence.EntityNotFoundException;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

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

	private boolean isFieldMissing(Field field) {
		boolean mandatory = field.getTemplateField().isMandatory();
		boolean linkEmpty = field.getLink() == null;
		boolean contentEmpty = field.getContent() == null || field.getContent().isEmpty();
		boolean isLink = field.getTemplateField().getType().equals(FieldTypes.LINK);
		boolean isDatetime = field.getTemplateField().getType().equals(FieldTypes.DATETIME);
		boolean isText = field.getTemplateField().getType().equals(FieldTypes.TEXT);

		return mandatory && ( (isLink && linkEmpty) || ((isText || isDatetime) && contentEmpty) );
	}

	public @Autowired ComponentResource(SQLComponentDao componentDatabase, SQLDaoFactoryUtil databaseFactory) {
		this.componentDatabase = componentDatabase;
		this.databaseFactory = databaseFactory;
	}

	@POST
	@Produces({MediaType.APPLICATION_XML,MediaType.APPLICATION_JSON})
	@Consumes({MediaType.APPLICATION_XML,MediaType.APPLICATION_JSON})
	public Long addComponent(Component component) throws TemplateNotFoundException, ComponentFieldIsMandatoryException, ComponentMissingFieldException {
		if(component.getTemplate() == null || !databaseFactory.getSqlTemplateDao().existsById(component.getTemplate().getId())) {
			throw new TemplateNotFoundException(component.getTemplate() == null ? "" : component.getTemplate().getId().toString(), component.getTemplate().getName());
		}

		//Check if fields are consistent between Template and Component
		List<Field> fields = component.getFields();
		List<TemplateField> templateFields = component.getTemplate().getFields();
		List<String> missingFields = new ArrayList<>();

		for (TemplateField field : templateFields) {
			if (fields.stream().noneMatch(f -> f.getName().equals(field.getName()))) {
				missingFields.add(field.getName());
			}
		}
		if(!missingFields.isEmpty()) {
			throw new ComponentMissingFieldException(component.getName(),
					Arrays.toString(missingFields.toArray()));
		}

		if(component.getFields().stream().anyMatch(this::isFieldMissing)) {
			throw new ComponentFieldIsMandatoryException(
					component.getFields().stream().map(
							(field) -> isFieldMissing(field) ? field.getTemplateField().getName() + ", " : ""
					).reduce("", String::concat));
		}

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

	@Path("/{id : \\d+}")
	@GET
	@Produces({MediaType.APPLICATION_XML,MediaType.APPLICATION_JSON})
	public Component getComponent(@PathParam("id") String idPath) throws ComponentNotFoundException {
		Long id = Long.parseLong(idPath);
		Component component = getComponentUtil(id);
		component.setTemplate(new Template(component.getTemplate().getId()));
		return component;
	}

	@Path("/{id : \\d+}")
	@POST
	@Consumes({MediaType.APPLICATION_XML,MediaType.APPLICATION_JSON})
	public void modifyComponent(@PathParam("id") String idPath, Component newComponent) throws ComponentNotFoundException {
		Component component;
		Long id = Long.parseLong(idPath);
		if(componentDatabase.existsById(id)) {
			component = componentDatabase.getReferenceById(id);
			component.setName(newComponent.getName());
			component.setDescription(newComponent.getDescription());
			componentDatabase.save(component);
		} else {
			throw new ComponentNotFoundException(idPath, newComponent.getName());
		}
	}

	@Path("/{id : \\d+}")
	@DELETE
	public void removeComponent(@PathParam("id") String idPath) throws ComponentNotFoundException {
		Long id = Long.parseLong(idPath);
		if(componentDatabase.existsById(id)) {
			componentDatabase.deleteById(id);
		} else {
			throw new ComponentNotFoundException(idPath, "");
		}
	}

	@Path("/{id : \\d+}/template")
	@GET
	@Produces({MediaType.APPLICATION_XML,MediaType.APPLICATION_JSON})
	public Template getParentTemplate(@PathParam("id") String idPath) throws ComponentNotFoundException {
		Long id = Long.parseLong(idPath);
		return componentDatabase.getReferenceById(id).getTemplate();
	}

	/// Fields

	@Path("/{id : \\d+}/fields")
	@GET
	@Produces({MediaType.APPLICATION_XML,MediaType.APPLICATION_JSON})
	public List<Field> getFieldsComponent(@PathParam("id") String idPath) throws ComponentNotFoundException {
		Long id = Long.parseLong(idPath);
		return getComponentUtil(id).getFields();
	}

	@Path("/{componentId : \\d+}/fields/{fieldName : \\S+}")
	@PUT
	@Consumes({MediaType.APPLICATION_XML,MediaType.APPLICATION_JSON})
	public void modifyFieldComponent(@PathParam("componentId") String idPath, @PathParam("fieldName") String fieldName, String value) throws ComponentFieldNotFoundException, ComponentFieldIsMandatoryException {
		Field componentField;

		if(databaseFactory.getSqlFieldDao().existsByName(fieldName)) {
			componentField = databaseFactory.getSqlFieldDao().getByName(fieldName);
			if(isFieldMissing(componentField) || value == null || value.isEmpty()) {
				throw new ComponentFieldIsMandatoryException(componentField.getTemplateField().getName());
			} else {
				if(componentField.getTemplateField().getType() == FieldTypes.LINK) {
					if(componentDatabase.existsById(Long.parseLong(value))) {
						try {
							componentField.setLink(new Component(Long.parseLong(value)));
						} catch (NumberFormatException e) {
							throw new ComponentFieldInvalidValueException(fieldName, value, componentField.getTemplateField().getType().toString());
						}
					} else {
						throw new ComponentNotFoundException(value, "");
					}
				}
				databaseFactory.getSqlFieldDao().save(componentField);
			}
		} else {
			throw new ComponentFieldNotFoundException(idPath, fieldName);
		}
	}

	@Path("/{componentId : \\d+}/fields/{fieldName : \\S+}")
	@GET
	@Produces({MediaType.APPLICATION_XML,MediaType.APPLICATION_JSON})
	public Field getFieldComponent(@PathParam("componentId") String idPath, @PathParam("fieldName") String fieldName) throws ComponentFieldNotFoundException {
		if(databaseFactory.getSqlFieldDao().existsByName(fieldName)) {
			return databaseFactory.getSqlFieldDao().getByName(fieldName);
		} else {
			throw new ComponentFieldNotFoundException(idPath, fieldName);
		}
	}

	@Path("/{componentId : \\d+}/fields/{fieldName : \\S+}")
	@DELETE
	public void removeComponentField(@PathParam("componentId") String idPath, @PathParam("fieldName") String fieldName) throws TemplateFieldNotFoundException {
		try {
			Field field = databaseFactory.getSqlFieldDao().getByName(fieldName);

			if(field.getTemplateField().isMandatory()) {
				throw new ComponentFieldIsMandatoryException(idPath);
			} else {
				field.setContent(null);
				databaseFactory.getSqlFieldDao().save(field);
			}
		} catch (EntityNotFoundException e) {
			throw new TemplateFieldNotFoundException(idPath, fieldName);
		}
	}
}