package gal.udc.fic.prperez.pleste.service;

import jakarta.ws.rs.ApplicationPath;
import org.glassfish.jersey.servlet.ServletContainer;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.annotation.Bean;

import java.util.HashMap;
import java.util.Map;

import static gal.udc.fic.prperez.pleste.service.Application.BASE_URL;

@ApplicationPath(BASE_URL)
@SpringBootApplication
public class Application extends SpringBootServletInitializer {
	public final static String BASE_URL = "/api/v0";

	public static void main(String[] args) {
		new Application().configure(new SpringApplicationBuilder(Application.class)).run(args);
	}

	@Bean
	public ServletRegistrationBean mainServlet(){
		ServletRegistrationBean registrationBean = new ServletRegistrationBean(new ServletContainer(), BASE_URL + "/*");

		Map<String,String> params = new HashMap<>();
		params.put("jersey.config.server.provider.packages", "gal.udc.fic.prperez.pleste.service");
		registrationBean.setInitParameters(params);
		registrationBean.setOrder(1);

		return registrationBean;
	}
}
