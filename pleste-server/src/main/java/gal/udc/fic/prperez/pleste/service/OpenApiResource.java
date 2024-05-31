package gal.udc.fic.prperez.pleste.service;

import io.swagger.v3.jaxrs2.integration.resources.BaseOpenApiResource;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.servlet.ServletConfig;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.*;
import jakarta.ws.rs.core.Application;

@Path("/openapi.json")
public class OpenApiResource extends BaseOpenApiResource {
	@Context
	ServletConfig config;

	@Context
	Application app;

	@GET
	@Produces({MediaType.APPLICATION_JSON})
	@Operation(hidden = true)
	public Response getOpenApi(@Context HttpHeaders headers,
	                           @Context UriInfo uriInfo) throws Exception {

		return super.getOpenApi(headers, config, app, uriInfo, "json");
	}
}
