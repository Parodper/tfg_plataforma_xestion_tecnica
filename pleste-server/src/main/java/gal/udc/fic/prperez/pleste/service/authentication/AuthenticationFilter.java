package gal.udc.fic.prperez.pleste.service.authentication;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.filter.GenericFilterBean;

import java.io.IOException;
import java.io.PrintWriter;

@Service
public class AuthenticationFilter extends GenericFilterBean {
	private final AuthenticationService authenticationService;

	public @Autowired AuthenticationFilter(AuthenticationService authenticationService) {
		this.authenticationService = authenticationService;
	}

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain filterChain) throws IOException, ServletException {
		try {
			Authentication authentication = authenticationService.getAuthentication((HttpServletRequest) request);
			SecurityContextHolder.getContext().setAuthentication(authentication);
		} catch (Exception exp) {
			HttpServletResponse httpResponse = (HttpServletResponse) response;
			httpResponse.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
			httpResponse.setContentType(MediaType.APPLICATION_JSON_VALUE);
			PrintWriter writer = httpResponse.getWriter();
			writer.print(exp.getMessage());
			writer.flush();
			writer.close();
		}

		filterChain.doFilter(request, response);
	}
}