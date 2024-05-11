package gal.udc.fic.prperez.pleste.service.exceptions;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.ws.rs.ClientErrorException;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

@Provider
public class RESTExceptionMapper implements ExceptionMapper<Exception> {
	@Override
	public Response toResponse(Exception exception) {
		if(exception instanceof RESTException restException){
			try {
				return Response.status(restException.getStatus())
						.entity(new ObjectMapper().writeValueAsString(restException))
						.type(MediaType.APPLICATION_JSON)
						.build();
			} catch (JsonProcessingException e) {
				throw new RuntimeException(e);
			}
		} else if (exception instanceof ClientErrorException clientErrorException) {
			return clientErrorException.getResponse();
		} else {
			throw new RuntimeException(exception);
		}
	}
}
