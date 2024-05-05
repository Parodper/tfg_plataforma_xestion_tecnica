package gal.udc.fic.prperez.pleste.service.authentication;

import gal.udc.fic.prperez.pleste.service.Application;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.AuthorizeHttpRequestsConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfig {
	private final AuthenticationFilter authenticationFilter;

	public @Autowired SecurityConfig(AuthenticationFilter authenticationFilter) {
		this.authenticationFilter = authenticationFilter;
	}

	private final String[] anonymousAccess = { "/openapi*", Application.BASE_URL + "/users/login" };

	@Bean
	public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
		http.csrf(AbstractHttpConfigurer::disable)
			.authorizeHttpRequests(
				authorizationManagerRequestMatcherRegistry ->
						authorizationManagerRequestMatcherRegistry.requestMatchers(anonymousAccess).anonymous())
			.authorizeHttpRequests(
				authorizationManagerRequestMatcherRegistry ->
						authorizationManagerRequestMatcherRegistry.requestMatchers(Application.BASE_URL + "/**").authenticated())
			.httpBasic(Customizer.withDefaults())
			.sessionManagement(
				httpSecuritySessionManagementConfigurer ->
						httpSecuritySessionManagementConfigurer.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
			.addFilterBefore(authenticationFilter, UsernamePasswordAuthenticationFilter.class);
		return http.build();
	}
}
