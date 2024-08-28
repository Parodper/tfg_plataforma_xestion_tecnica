package gal.udc.fic.prperez.pleste.client.view;

import gal.udc.fic.prperez.pleste.client.exceptions.BadRequestException;
import gal.udc.fic.prperez.pleste.client.exceptions.InternalErrorException;
import jakarta.servlet.http.HttpSession;
import org.openapitools.client.ApiException;
import org.openapitools.client.api.DefaultApi;
import org.openapitools.client.model.Component;
import org.openapitools.client.model.Template;
import org.openapitools.client.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.stream.Collectors;

@Controller
public class Search {
	private final DefaultApi defaultApi;

	public @Autowired Search(DefaultApi defaultApi) {
		this.defaultApi = defaultApi;
	}

	@GetMapping("/search")
	public String displayTemplate(@RequestParam(name = "search-input", required = false) String query,
	                              @RequestParam(name = "search-type", required = false) String type,
	                              @RequestParam(name = "from", required = false) String fromString,
	                              @RequestParam(name = "count", required = false) String countString,
								  @RequestParam(name = "clear-cache", required = false) String clearCache,
	                              Model model, HttpSession session) {
		CommonView.setModel(model, session);

		if(query != null) {
			model.addAttribute("from", fromString);
			model.addAttribute("count", countString);
			model.addAttribute("query", query);
			model.addAttribute("type", type);

			try {
				int from;
				int count;
				try {
					from = Integer.parseInt(fromString);
				} catch (NumberFormatException ignore) {
					from = 0;
				}
				try {
					count = Integer.parseInt(countString);
				} catch (NumberFormatException ignore) {
					count = 10;
				}

				if(clearCache != null) {
					switch (type) {
						case "COMPONENT" -> defaultApi.clearComponentCache(query);
						case "TEMPLATE" -> defaultApi.clearTemplateCache(query);
						case "USER" -> defaultApi.clearUserCache(query);
						default -> throw new BadRequestException(type + " non é un tipo válido");
					}
				}

				List<?> result = switch (type) {
					case "COMPONENT" -> defaultApi.searchComponents(query, from, count);
					case "TEMPLATE" -> defaultApi.searchTemplates(query, from, count);
					case "USER" -> defaultApi.searchUsers(query, from, count);
					default -> throw new BadRequestException(type + " non é un tipo válido");
				};

				model.addAttribute("results_list",
						result.stream().map(this::getName));
				model.addAttribute("url_path",
						result.stream().collect(Collectors.toMap(this::getName, this::getId)));
			} catch (ApiException e) {
				throw new InternalErrorException(e.getMessage());
			}
		} else {
			model.addAttribute("from", "0");
			model.addAttribute("count", "10");
		}

		return "search.html";
	}

	private String getName(Object o) {
		if (o instanceof Component c) {
			return c.getName();
		} else if (o instanceof Template t) {
			return t.getName();
		} else if (o instanceof User u) {
			return u.getUsername();
		} else {
			return null;
		}
	}

	private Long getId(Object o) {
		if (o instanceof Component c) {
			return c.getId();
		} else if (o instanceof Template t) {
			return t.getId();
		} else if (o instanceof User u) {
			return u.getId();
		} else {
			return null;
		}
	}
}
