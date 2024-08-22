package gal.udc.fic.prperez.pleste.service;

import gal.udc.fic.prperez.pleste.service.dao.SQLDaoFactoryUtil;
import gal.udc.fic.prperez.pleste.service.dao.component.Component;
import gal.udc.fic.prperez.pleste.service.dao.template.Template;
import gal.udc.fic.prperez.pleste.service.dao.users.User;
import gal.udc.fic.prperez.pleste.service.search.FindListener;
import gal.udc.fic.prperez.pleste.service.search.Node;
import gal.udc.fic.prperez.pleste.service.search.RootNode;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.servers.Server;
import jakarta.validation.constraints.NotNull;
import jakarta.ws.rs.*;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTreeWalker;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.apache.commons.collections4.map.LRUMap;

import java.util.ArrayList;
import java.util.List;

@Path("/search")
@Service
@Transactional
@OpenAPIDefinition(
		info = @Info(
				title = "pleste-service-search",
				version = "0.1.0"),
		servers = {
				@Server(
						url = "http://" + Application.DOMAIN + Application.BASE_URL)
		})
public class SearchResource {
	private final SQLDaoFactoryUtil sqlDaoFactoryUtil;
	private final LRUMap<String, List<Template>> searchResultTemplatesCache;
	private final LRUMap<String, List<Component>> searchResultComponentsCache;
	private final LRUMap<String, List<User>> searchResultUsersCache;

	@Autowired
	public SearchResource(SQLDaoFactoryUtil sqlDaoFactoryUtil) {
		this.sqlDaoFactoryUtil = sqlDaoFactoryUtil;
		this.searchResultTemplatesCache = new LRUMap<>();
		this.searchResultComponentsCache = new LRUMap<>();
		this.searchResultUsersCache = new LRUMap<>();
	}

	@Path("/templates")
	@GET
	public List<Template> searchTemplates(@QueryParam("query") String query, @QueryParam("skip") Integer skip, @QueryParam("count") Integer count) {
		List<Template> result;
		if (searchResultTemplatesCache.containsKey(query)) {
			result = searchResultTemplatesCache.get(query);
		} else {
			Node rootNode = generateTree(query);
			result = sqlDaoFactoryUtil.getSqlTemplateDao().findAll().stream().filter(rootNode::matches).toList();
			searchResultTemplatesCache.put(query, result);
		}

		int from = skip == null ? 0 : skip;
		int to = count == null ? result.size() : count + from;

		if (to > result.size()) {
			to = result.size();
		}

		try {
			return result.subList(from, to);
		} catch (IndexOutOfBoundsException e) {
			return new ArrayList<>();
		}
	}

	@Path("/components")
	@GET
	public List<Component> searchComponents(@QueryParam("query") String query, @QueryParam("skip") Integer skip, @QueryParam("count") Integer count) {
		List<Component> result;
		if (searchResultComponentsCache.containsKey(query)) {
			result = searchResultComponentsCache.get(query);
		} else {
			Node rootNode = generateTree(query);
			result = sqlDaoFactoryUtil.getSqlComponentDao().findAll().stream().filter(rootNode::matches).toList();
			searchResultComponentsCache.put(query, result);
		}

		int from = skip == null ? 0 : skip;
		int to = count == null ? result.size() : count + from;

		if (to > result.size()) {
			to = result.size();
		}

		try {
			return result.subList(from, to);
		} catch (IndexOutOfBoundsException e) {
			return new ArrayList<>();
		}
	}

	@Path("/users")
	@GET
	public List<User> searchUsers(@QueryParam("query") String query, @QueryParam("skip") Integer skip, @QueryParam("count") Integer count) {
		List<User> result;
		if (searchResultUsersCache.containsKey(query)) {
			result = searchResultUsersCache.get(query);
		} else {
			Node rootNode = generateTree(query);
			result = sqlDaoFactoryUtil.getSqlUserDao().findAll().stream().filter(rootNode::matches).toList();
			searchResultUsersCache.put(query, result);
			return result;
		}

		int from = skip == null ? 0 : skip;
		int to = count == null ? result.size() : count + from;

		if (to > result.size()) {
			to = result.size();
		}

		try {
			return result.subList(from, to);
		} catch (IndexOutOfBoundsException e) {
			return new ArrayList<>();
		}
	}

	@Path("/templates/cache")
	@DELETE
	public void clearTemplateCache(@QueryParam("query") String query) {
		if (query == null) {
			searchResultTemplatesCache.clear();
		} else {
			searchResultTemplatesCache.remove(query);
		}
	}

	@Path("/components/cache")
	@DELETE
	public void clearComponentCache(@QueryParam("query") String query) {
		if (query == null) {
			searchResultComponentsCache.clear();
		} else {
			searchResultComponentsCache.remove(query);
		}
	}

	@Path("/users/cache")
	@DELETE
	public void clearUserCache(@QueryParam("query") String query) {
		if (query == null) {
			searchResultUsersCache.clear();
		} else {
			searchResultUsersCache.remove(query);
		}
	}

	@NotNull
	private Node generateTree(String query) {
		FindLexer lexer = new FindLexer(CharStreams.fromString(query));
		CommonTokenStream tokens = new CommonTokenStream(lexer);
		FindParser parser = new FindParser(tokens);

		FindParser.TopContext context = parser.top();
		Node root = new RootNode();
		ParseTreeWalker.DEFAULT.walk(new FindListener(root), context);

		return root;
	}
}
