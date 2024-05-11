package gal.udc.fic.prperez.pleste.service.exceptions;

import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

@Provider
public class RESTExceptionMapper implements ExceptionMapper<Exception> {
	@Override
	public Response toResponse(Exception exception) {
		if(exception instanceof RESTException restException){
			return Response.status(restException.getStatus())
					.entity("\"" + restException.getMessage() + "\"")
					.type(MediaType.APPLICATION_JSON)
					.build();
		} else {
			throw new RuntimeException(exception);
		}
	}
}
