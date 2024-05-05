package gal.udc.fic.prperez.pleste.client.service;

import okhttp3.Interceptor.Chain;
import okhttp3.Request;
import okhttp3.Response;
import org.springframework.web.context.annotation.RequestScope;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;

import java.io.IOException;

public class SecureServiceFactory {
	private static final String AUTH_TOKEN_HEADER_NAME = "X-API-KEY";

	private SecureServiceFactory() {}

	public static Response addAPIHeader(Chain chain) throws IOException {
		var requestContext = RequestContextHolder.getRequestAttributes();

		Request request = chain.request();
		Request.Builder requestBuilder = request.newBuilder();
		if(request.header(AUTH_TOKEN_HEADER_NAME) == null) {
			requestBuilder.addHeader(AUTH_TOKEN_HEADER_NAME, (String) requestContext.getAttribute("token", RequestAttributes.SCOPE_SESSION));
		}

		return chain.proceed(requestBuilder.build());
	}
}
