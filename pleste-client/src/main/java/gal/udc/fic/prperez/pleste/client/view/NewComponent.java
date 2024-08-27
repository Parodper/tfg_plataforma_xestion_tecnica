package gal.udc.fic.prperez.pleste.client.view;

import gal.udc.fic.prperez.pleste.client.exceptions.BadRequestException;
import gal.udc.fic.prperez.pleste.client.exceptions.InternalErrorException;
import gal.udc.fic.prperez.pleste.client.exceptions.MissingMandatoryFieldException;
import gal.udc.fic.prperez.pleste.client.exceptions.ObjectNotFoundException;
import jakarta.servlet.http.HttpSession;
import org.openapitools.client.ApiException;
import org.openapitools.client.api.DefaultApi;
import org.openapitools.client.model.*;
import org.openapitools.client.model.Component;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeParseException;
import java.util.*;

@Controller
public class NewComponent {
	private final DefaultApi defaultApi;

	public @Autowired NewComponent(DefaultApi defaultApi) {
		this.defaultApi = defaultApi;
	}

	@GetMapping("/newcomponent")
	public String newComponent(@RequestParam(name = "template", required = false) String templateIdParam, Model model, HttpSession session) {
		if(templateIdParam == null) {
			try {
				templateIdParam = defaultApi.getAllTemplates().get(0).getId().toString();
			} catch (ApiException | NullPointerException e) {
				throw new InternalErrorException(e.getMessage());
			}
		}

		CommonView.setModel(model, session);
		model.addAttribute("selected_template", templateIdParam);

		try {
			model.addAttribute("templates", defaultApi.getAllTemplates());
			model.addAttribute("fields", defaultApi.getTemplate(templateIdParam).getFields());
		} catch (ApiException e) {
			if(e.getCode() == HttpStatus.NOT_FOUND.value()) {
				throw new ObjectNotFoundException("modelo", templateIdParam);
			} else {
				throw new InternalErrorException(e.getMessage());
			}
		}

		return "newcomponent.html";
	}

	@PostMapping(value = "/newcomponent", consumes = "application/x-www-form-urlencoded;charset=UTF-8")
	public ResponseEntity<String> createNewComponent(@RequestParam(name = "template") String templateIdParam, @RequestParam Map<String, String> fields) {
		Long newId;
		Template template;
		Component component = new Component();
		Map<String, String> fieldMap = new HashMap<>();

		if(templateIdParam == null) {
			try {
				templateIdParam = defaultApi.getAllTemplates().get(0).getId().toString();
			} catch (ApiException | NullPointerException e) {
				throw new InternalErrorException(e.getMessage());
			}
		} else if (templateIdParam.contains(",")) {
			templateIdParam = templateIdParam.split(",")[0];
		}

		component.setName(fields.get("component_name"));
		component.setDescription(fields.get("component_description"));

		for(String field : fields.keySet()) {
			if(field.matches("^field_.*")) {
				String fieldName = field.substring("field_".length());
				String fieldValue = fields.get(field);

				fieldMap.put(fieldName, fieldValue);
			}
		}

		try {
			List<String> missingFields = new ArrayList<>();
			List<FieldObject> localFields = new ArrayList<>();
			template = defaultApi.getTemplate(templateIdParam);

			for(TemplateField templateField : template.getFields()) {
				if(
						(!fieldMap.containsKey(templateField.getName()) || fieldMap.get(templateField.getName()).isEmpty()) && templateField.getMandatory()) {
					missingFields.add(templateField.getName());
				} else {
					FieldObject tmpField = new FieldObject();
					tmpField.setTemplateField(new TemplateField().id(templateField.getId()));
					tmpField.setType(
							CommonView.convertTypeEnum(
									defaultApi.getFieldTemplate(template.getId().toString(), templateField.getId().toString()).getType()));
					String fieldValue = fieldMap.get(templateField.getName());

					try {
						switch (tmpField.getType()) {
							case LINK -> {
								if(fieldMap.get(templateField.getName()).isEmpty()) {
									tmpField.setContent(new FieldObjectContent());
								} else {
									defaultApi.getComponent(fieldValue);
									tmpField.setContent(new FieldObjectContent(
											new Component().id(Long.parseLong(fieldValue))
									));
								}
							}
							case TEXT -> tmpField.setContent(new FieldObjectContent(fieldMap.get(templateField.getName())));
							case DATETIME -> tmpField.setContent(new FieldObjectContent(new JSONDatetime().datetime(
									LocalDateTime.parse(fieldValue).atZone(ZoneId.systemDefault()).toOffsetDateTime()
							)));
							case NUMBER -> tmpField.setContent(new FieldObjectContent(new BigDecimal(fieldValue)));
						}
					} catch (NumberFormatException | DateTimeParseException | ApiException e) {
						if (e instanceof ApiException ae) {
							if(ae.getCode() == HttpStatus.NOT_FOUND.value()) {
								throw new BadRequestException("Non se atopou o compoñente con ID " + fieldValue);
							} else {
								throw e;
							}
						} else {
							throw new BadRequestException("Datos inválidos");
						}
					}
					localFields.add(tmpField);
				}
			}

			if(!missingFields.isEmpty()) {
				throw new MissingMandatoryFieldException(missingFields);
			}

			component.setFields(localFields);
			component.setTemplate( new Template().id(template.getId()) );
			newId = defaultApi.addComponent(component);
		} catch (ApiException e) {
			throw new InternalErrorException(e.getMessage());
		}

		return ResponseEntity
				.status(HttpStatus.SEE_OTHER)
				.header("Location","/component?id=" + newId)
				.body("Creado compoñente nº" + newId);
	}
}
