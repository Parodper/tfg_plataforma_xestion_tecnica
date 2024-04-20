package gal.udc.fic.prperez.pleste.service;

import io.swagger.v3.jaxrs2.integration.resources.OpenApiResource;
import org.glassfish.jersey.server.ResourceConfig;
import org.springframework.stereotype.Component;

@Component
public class JerseyConfig extends ResourceConfig {
	public JerseyConfig() {
		register(OpenApiResource.class);
		register(TemplateResource.class);
	}
}