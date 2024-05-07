package gal.udc.fic.prperez.pleste.client.service;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.openapitools.client.api.DefaultApi;
import org.openapitools.client.ApiClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;

import java.io.IOException;

@Configuration
public class ApiClientConfiguration {

	@Bean
	public DefaultApi defaultApi() {
		return new DefaultApi(apiClient());
	}

	@Bean
	public ApiClient apiClient() {
		return new ApiClient(new OkHttpClient.Builder()
				.addInterceptor(ApiClientConfiguration::addAPIHeader)
				.build());
	}

	private static final String AUTH_TOKEN_HEADER_NAME = "X-API-KEY";
	public static Response addAPIHeader(Interceptor.Chain chain) throws IOException {
		var requestContext = RequestContextHolder.getRequestAttributes();

		Request request = chain.request();
		Request.Builder requestBuilder = request.newBuilder();
		Object token = requestContext.getAttribute("token", RequestAttributes.SCOPE_SESSION);
		if(request.header(AUTH_TOKEN_HEADER_NAME) == null && token != null) {
			requestBuilder.addHeader(AUTH_TOKEN_HEADER_NAME, (String) token);
		}

		return chain.proceed(requestBuilder.build());
	}
}