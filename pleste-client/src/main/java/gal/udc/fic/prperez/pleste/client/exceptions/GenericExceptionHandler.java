package gal.udc.fic.prperez.pleste.client.exceptions;

import gal.udc.fic.prperez.pleste.client.view.Error;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.ModelAndView;

@RestControllerAdvice
public class GenericExceptionHandler {
	@ExceptionHandler
	public ModelAndView handle(RuntimeException e) {
		HttpStatus status;

		try {
			ResponseStatus annotation = e.getClass().getAnnotation(ResponseStatus.class);
			status = annotation.value();
		} catch (Exception ignored) {
			status = HttpStatus.INTERNAL_SERVER_ERROR;
		}

		ModelAndView errorPage = new Error().error(e.getMessage());
		errorPage.setStatus(status);

		return errorPage;
	}
}


