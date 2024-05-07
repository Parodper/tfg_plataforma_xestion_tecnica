package gal.udc.fic.prperez.pleste.client;

import gal.udc.fic.prperez.pleste.client.service.LocalAuthenticationProvider;
import gal.udc.fic.prperez.pleste.client.service.LocalLogoutHandler;
import org.openapitools.client.api.DefaultApi;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {
	private final DefaultApi defaultApi;

	public @Autowired SecurityConfig(DefaultApi defaultApi) {
		this.defaultApi = defaultApi;
	}

	@Bean
	public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
		http.csrf(AbstractHttpConfigurer::disable)
				.authorizeHttpRequests(
						authorizationManagerRequestMatcherRegistry ->
								authorizationManagerRequestMatcherRegistry.requestMatchers("/favicon*").anonymous())
				.authorizeHttpRequests(
						authorizationManagerRequestMatcherRegistry ->
								authorizationManagerRequestMatcherRegistry.requestMatchers("/login*").anonymous())
				.authorizeHttpRequests(
						authorizationManagerRequestMatcherRegistry ->
								authorizationManagerRequestMatcherRegistry.requestMatchers("/error*").anonymous())
				.authorizeHttpRequests(
						authorizationManagerRequestMatcherRegistry ->
								authorizationManagerRequestMatcherRegistry.requestMatchers("/**").authenticated())
				.formLogin(httpSecurityFormLoginConfigurer ->
						httpSecurityFormLoginConfigurer
								.loginPage("/login")
								.successForwardUrl("/")
								.failureForwardUrl("/login?error=true"))
				.logout(
						httpSecurityLogoutConfigurer -> httpSecurityLogoutConfigurer
								.addLogoutHandler(new LocalLogoutHandler(defaultApi)));
		return http.build();
	}

	@Bean
	public LocalAuthenticationProvider localAuthenticationProvider() {
		return new LocalAuthenticationProvider(defaultApi);
	}
}