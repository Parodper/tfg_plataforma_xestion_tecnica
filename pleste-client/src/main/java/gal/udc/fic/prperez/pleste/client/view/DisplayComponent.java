package gal.udc.fic.prperez.pleste.client.view;

import gal.udc.fic.prperez.pleste.client.exceptions.InternalErrorException;
import gal.udc.fic.prperez.pleste.client.exceptions.ObjectNotFoundException;
import jakarta.servlet.http.HttpSession;
import org.openapitools.client.ApiException;
import org.openapitools.client.api.DefaultApi;
import org.openapitools.client.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

@Controller
public class DisplayComponent {
	private final DefaultApi defaultApi;

	public @Autowired DisplayComponent(DefaultApi defaultApi) {
		this.defaultApi = defaultApi;
	}

	@GetMapping("/component")
	public String displayComponent(@RequestParam(name = "id") String idPath, Model model, HttpSession session) {
		CommonView.setModel(model, session);

		try {
			Template template;
			Component component = defaultApi.getComponent(idPath);
			template = defaultApi.getTemplate(component.getTemplate().getId().toString());
			Map<String, String> fieldValues = new HashMap<>();
			Map<String, Long> linkValues = new HashMap<>();

			for(FieldObject f : component.getFields()) {
				try {
					switch (f.getTemplateField().getType()) {
						case TEXT -> fieldValues.put(f.getTemplateField().getName(), f.getContent().getString());
						case DATETIME ->
								fieldValues.put(f.getTemplateField().getName(),
										f.getContent().getJSONDatetime().getDatetime()
												.format(DateTimeFormatter
														.ofLocalizedDateTime(FormatStyle.MEDIUM)
														.localizedBy(Locale.forLanguageTag("gl"))));
						case LINK -> {
							//Dereference ID to Components
							Long id = f.getContent().getComponent().getId();
							try {
								fieldValues.put(f.getTemplateField().getName(), defaultApi.getComponent(id.toString()).getName());
							} catch (ApiException e) {
								fieldValues.put(f.getTemplateField().getName(), id.toString());
							}
							linkValues.put(f.getTemplateField().getName(), id);
						}
						case NUMBER ->
								fieldValues.put(f.getTemplateField().getName(), f.getContent().getBigDecimal().toString());
					}
				} catch (NullPointerException ignore) {
					fieldValues.put(f.getTemplateField().getName(), "");
				}
			}

			model.addAttribute("component", component);
			model.addAttribute("template", template);
			model.addAttribute("values", fieldValues);
			model.addAttribute("links", linkValues);
		} catch (ApiException e) {
			if(e.getCode() == 404) {
				throw new ObjectNotFoundException("compoñente", idPath);
			} else {
				throw new InternalErrorException(e.getMessage());
			}
		}

		return "displaycomponent.html";
	}
}
