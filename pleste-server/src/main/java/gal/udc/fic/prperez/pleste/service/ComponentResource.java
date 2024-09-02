package gal.udc.fic.prperez.pleste.service;

import gal.udc.fic.prperez.pleste.service.dao.SQLDaoFactoryUtil;
import gal.udc.fic.prperez.pleste.service.dao.component.*;
import gal.udc.fic.prperez.pleste.service.dao.template.Template;
import gal.udc.fic.prperez.pleste.service.dao.template.TemplateField;
import gal.udc.fic.prperez.pleste.service.exceptions.RESTExceptionSerializable;
import gal.udc.fic.prperez.pleste.service.exceptions.component.*;
import gal.udc.fic.prperez.pleste.service.exceptions.TemplateFieldNotFoundException;
import gal.udc.fic.prperez.pleste.service.exceptions.template.TemplateNotFoundException;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.servers.Server;
import jakarta.persistence.EntityNotFoundException;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.format.DateTimeParseException;
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
						url = "http://" + Application.DOMAIN + Application.BASE_URL)
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

	private boolean isFieldMissing(Field<?> field) {
		TemplateField templateField = field.getTemplateField(); //databaseFactory.getSqlTemplateFieldDao().getReferenceById(field.getTemplateField().getId());

		boolean mandatory = templateField.isMandatory();
		boolean contentEmpty;
		if(field.getContent() == null) {
			contentEmpty = true;
		} else {
			if(field.getContent() instanceof String f) {
				contentEmpty = f.isEmpty();
			} else {
				contentEmpty = false;
			}
		}

		return mandatory && contentEmpty;
	}

	public @Autowired ComponentResource(SQLComponentDao componentDatabase, SQLDaoFactoryUtil databaseFactory) {
		this.componentDatabase = componentDatabase;
		this.databaseFactory = databaseFactory;
	}

	@POST
	@Produces({MediaType.APPLICATION_JSON})
	@Consumes({MediaType.APPLICATION_JSON})
	@ApiResponses(value = {
			@ApiResponse(description = "Returns created component ID", responseCode = "200",
					content = @Content(schema = @Schema(implementation = Long.class))),
			@ApiResponse(description = "Template not found", responseCode = "404",
					content = @Content(schema = @Schema(implementation = RESTExceptionSerializable.class))),
			@ApiResponse(description = "Mandatory field is missing", responseCode = "400",
					content = @Content(schema = @Schema(implementation = RESTExceptionSerializable.class)))
	})
	public Long addComponent(Component component) throws TemplateNotFoundException, ComponentFieldIsMandatoryException {
		if(component.getTemplate() == null ||
				!databaseFactory.getSqlTemplateDao().existsById(component.getTemplate().getId())) {
			throw new TemplateNotFoundException(component.getTemplate() == null ? "" : component.getTemplate().getId().toString(), component.getTemplate().getName());
		}

		//Check if fields are consistent between Template and Component
		List<Field<?>> fields = component.getFields();
		List<TemplateField> templateFields = databaseFactory.getSqlTemplateDao().getReferenceById(component.getTemplate().getId()).getFields();

		for (TemplateField field : templateFields) {
			if (fields.stream().noneMatch(f -> f.getTemplateField().getId().equals(field.getId()))) {
				fields.add(switch (field.getType()) {
					case TEXT -> new TextField(null, null, field);
					case DATETIME -> new DatetimeField(null, null, field);
					case LINK -> new LinkField(null, null, field);
					case NUMBER -> new NumberField(null, null, field);
				});
			}
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
	@Produces({MediaType.APPLICATION_JSON})
	@ApiResponses(value = {
			@ApiResponse(description = "Returns a list of all components", responseCode = "200",
					content = @Content(array = @ArraySchema(schema = @Schema(implementation = Component.class)))),
	})
	public List<Component> getAllComponents(@QueryParam("skip") Integer skip, @QueryParam("count") Integer count) {
		if(count == null) {
			return componentDatabase.findAll();
		} else {
			skip = skip == null ? 0 : skip;

			Pageable page = PageRequest.of(skip, count, Sort.by("id"));

			return componentDatabase.findAll(page).toList();
		}
	}

	@Path("/find")
	@Produces({MediaType.APPLICATION_JSON})
	@ApiResponses(value = {
			@ApiResponse(description = "Returns a list of all matching components", responseCode = "200",
					content = @Content(array = @ArraySchema(schema = @Schema(implementation = Component.class))))
	})
	public List<Component> getComponentsByName(@QueryParam("name") String name) {
		return componentDatabase.findByName(name);
	}

	@Path("/{id : \\d+}")
	@GET
	@Produces({MediaType.APPLICATION_JSON})
	@ApiResponses(value = {
			@ApiResponse(description = "Returns component. The template object might only have the ID initialized", responseCode = "200",
					content = @Content(schema = @Schema(implementation = Component.class))),
			@ApiResponse(description = "Component not found", responseCode = "404",
					content = @Content(schema = @Schema(implementation = RESTExceptionSerializable.class)))
	})
	public Component getComponent(@PathParam("id") String idPath) throws ComponentNotFoundException {
		Long id = Long.parseLong(idPath);
		Component component = getComponentUtil(id);
		component.setTemplate(new Template(component.getTemplate().getId()));
		return component;
	}

	@Path("/{id : \\d+}")
	@POST
	@Consumes({MediaType.APPLICATION_JSON})
	@ApiResponses(value = {
			@ApiResponse(description = "Successfully modified", responseCode = "204"),
			@ApiResponse(description = "Component not found", responseCode = "404",
					content = @Content(schema = @Schema(implementation = RESTExceptionSerializable.class)))
	})
	public void modifyComponent(@PathParam("id") String idPath, Component newComponent) throws ComponentNotFoundException {
		Component component;
		Long id = Long.parseLong(idPath);
		if(componentDatabase.existsById(id)) {
			component = componentDatabase.getReferenceById(id);
			component.setName(newComponent.getName());
			component.setDescription(newComponent.getDescription());
			for(Field<?> field : component.getFields()) {
				try {
					switch (field.getTemplateField().getType()) {
						case TEXT -> ((TextField) field).setContent((String)
								newComponent.getFields().stream()
										.filter(f -> f.getTemplateField().equals(field.getTemplateField()))
										.findFirst()
										.get()
										.getContent());
						case DATETIME -> ((DatetimeField) field).setContent((JSONDatetime)
								newComponent.getFields().stream()
										.filter(f -> f.getTemplateField().equals(field.getTemplateField()))
										.findFirst()
										.get()
										.getContent());
						case LINK -> ((LinkField) field).setContent((Component)
								newComponent.getFields().stream()
										.filter(f -> f.getTemplateField().equals(field.getTemplateField()))
										.findFirst()
										.get()
										.getContent());
						case NUMBER -> ((NumberField) field).setContent((BigDecimal)
								newComponent.getFields().stream()
										.filter(f -> f.getTemplateField().equals(field.getTemplateField()))
										.findFirst()
										.get()
										.getContent());
					}
				} catch (NoSuchElementException | ClassCastException ignore) {
					throw new ComponentFieldNotFoundException(idPath, field.getTemplateField().getName());
				}
				databaseFactory.getSqlFieldDao().save(field);
			}
			componentDatabase.save(component);
		} else {
			throw new ComponentNotFoundException(idPath, newComponent.getName());
		}
	}

	@Path("/{id : \\d+}")
	@DELETE
	@ApiResponses(value = {
			@ApiResponse(description = "Successfully deleted", responseCode = "204"),
			@ApiResponse(description = "Component not found", responseCode = "404",
					content = @Content(schema = @Schema(implementation = RESTExceptionSerializable.class)))
	})
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
	@Produces({MediaType.APPLICATION_JSON})
	@ApiResponses(value = {
			@ApiResponse(description = "Returns the template object this is based on", responseCode = "200",
					content = @Content(schema = @Schema(implementation = Template.class))),
			@ApiResponse(description = "Component not found", responseCode = "404",
					content = @Content(schema = @Schema(implementation = RESTExceptionSerializable.class)))
	})
	public Template getParentTemplate(@PathParam("id") String idPath) throws ComponentNotFoundException {
		Long id = Long.parseLong(idPath);
		return componentDatabase.getReferenceById(id).getTemplate();
	}

	/// Fields

	@Path("/{id : \\d+}/fields")
	@GET
	@Produces({MediaType.APPLICATION_JSON})
	@ApiResponses(value = {
			@ApiResponse(description = "Returns the list of fields of this component", responseCode = "200",
					content = @Content(array = @ArraySchema(schema = @Schema(implementation = Field.class)))),
			@ApiResponse(description = "Component not found", responseCode = "404",
					content = @Content(schema = @Schema(implementation = RESTExceptionSerializable.class)))
	})
	public List<Field> getFieldsComponent(@PathParam("id") String idPath) throws ComponentNotFoundException {
		Long id = Long.parseLong(idPath);
		return getComponentUtil(id).getFields().stream().map(f -> (Field) f).toList();
	}

	@Path("/{componentId : \\d+}/fields/{fieldName : \\S+}")
	@PUT
	@Consumes({MediaType.APPLICATION_JSON})
	@ApiResponses(value = {
			@ApiResponse(description = "Successfully modified", responseCode = "204"),
			@ApiResponse(description = "Component or the field not found", responseCode = "404",
					content = @Content(schema = @Schema(implementation = RESTExceptionSerializable.class))),
			@ApiResponse(description = "Field is mandatory, and the provided value is null", responseCode = "400",
					content = @Content(schema = @Schema(implementation = RESTExceptionSerializable.class)))
	})
	public void modifyFieldComponent(@PathParam("componentId") String idPath, @PathParam("fieldName") String fieldName, JSONString valueString) throws ComponentFieldNotFoundException, ComponentFieldIsMandatoryException {
		Field<?> componentField;

		if(databaseFactory.getSqlFieldDao().existsByTemplateFieldName(fieldName)) {
			componentField = databaseFactory.getSqlFieldDao().getByTemplateFieldName(fieldName);
			if(valueString == null) {
				if(componentField.getTemplateField().isMandatory()) {
					throw new ComponentFieldIsMandatoryException(componentField.getTemplateField().getName());
				} else {
					componentField.setContent(null);
				}
			} else {
				switch (componentField.getType()) {
					case TEXT -> {
						String value = valueString.getContent();
						((TextField) componentField).setContent(value);
					}
					case DATETIME -> {
						try {
							JSONDatetime value = JSONDatetime.parse(valueString.getContent());
							((DatetimeField) componentField).setContent(value);
						} catch (DateTimeParseException e) {
							throw new ComponentFieldInvalidValueException(fieldName, "DATETIME", valueString.getContent());
						}
					}
					case LINK -> {
						try {
							Component value = databaseFactory.getSqlComponentDao().getReferenceById(Long.parseLong(valueString.getContent()));
							((LinkField) componentField).setContent(value);
						} catch (EntityNotFoundException e) {
							throw new ComponentFieldInvalidValueException(fieldName, "LINK", valueString.getContent());
						}
					}
					case NUMBER -> {
						try {
							BigDecimal value = new BigDecimal(valueString.getContent());
							((NumberField) componentField).setContent(value);
						} catch (EntityNotFoundException e) {
							throw new ComponentFieldInvalidValueException(fieldName, "NUMBER", valueString.getContent());
						}
					}
				}
			}
			databaseFactory.getSqlFieldDao().save(componentField);
		} else {
			throw new ComponentFieldNotFoundException(idPath, fieldName);
		}
	}

	@Path("/{componentId : \\d+}/fields/{fieldName : \\S+}")
	@GET
	@Produces({MediaType.APPLICATION_JSON})
	@ApiResponses(value = {
			@ApiResponse(description = "Returns the requested field", responseCode = "200",
					content = @Content(schema = @Schema(implementation = Field.class))),
			@ApiResponse(description = "Component, or the field, not found", responseCode = "404",
					content = @Content(schema = @Schema(implementation = RESTExceptionSerializable.class)))
	})
	public Field getFieldComponent(@PathParam("componentId") String idPath, @PathParam("fieldName") String fieldName) throws ComponentFieldNotFoundException {
		if(databaseFactory.getSqlFieldDao().existsByTemplateFieldName(fieldName)) {
			return databaseFactory.getSqlFieldDao().getByTemplateFieldName(fieldName);
		} else {
			throw new ComponentFieldNotFoundException(idPath, fieldName);
		}
	}

	@Path("/{componentId : \\d+}/fields/{fieldName : \\S+}")
	@DELETE
	@ApiResponses(value = {
			@ApiResponse(description = "Successfully deleted", responseCode = "204"),
			@ApiResponse(description = "Component, or the field, not found", responseCode = "404",
					content = @Content(schema = @Schema(implementation = RESTExceptionSerializable.class)))
	})
	public void removeComponentField(@PathParam("componentId") String idPath, @PathParam("fieldName") String fieldName) throws TemplateFieldNotFoundException {
		try {
			Field<?> field = databaseFactory.getSqlFieldDao().getByTemplateFieldName(fieldName);

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
