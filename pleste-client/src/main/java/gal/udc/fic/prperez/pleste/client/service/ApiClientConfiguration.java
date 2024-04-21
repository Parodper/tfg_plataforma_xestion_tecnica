package gal.udc.fic.prperez.pleste.client.service;

import org.openapitools.client.api.DefaultApi;
import org.openapitools.client.ApiClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ApiClientConfiguration {

	@Bean
	public DefaultApi defaultApi() {
		return new DefaultApi(apiClient());
	}

	@Bean
	public ApiClient apiClient() {
		return new ApiClient();
	}
}