package gal.udc.fic.prperez.pleste.client;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {
	@Bean
	public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
		http.csrf(AbstractHttpConfigurer::disable)
				.authorizeHttpRequests(
						authorizationManagerRequestMatcherRegistry ->
								authorizationManagerRequestMatcherRegistry.requestMatchers("/login").anonymous())
				.authorizeHttpRequests(
						authorizationManagerRequestMatcherRegistry ->
								authorizationManagerRequestMatcherRegistry.requestMatchers("/**").authenticated())
				.formLogin(httpSecurityFormLoginConfigurer ->
						httpSecurityFormLoginConfigurer.loginPage("/login")
								.successForwardUrl("/")
								.failureForwardUrl("/login?error=true"))
				.logout(
						httpSecurityLogoutConfigurer ->
							httpSecurityLogoutConfigurer.logoutSuccessUrl("/login")
									.logoutUrl("/logout"));
		return http.build();
	}
}