package gal.udc.fic.prperez.pleste.client.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.List;

@ResponseStatus(value = HttpStatus.BAD_REQUEST)
public class MissingMandatoryFieldException extends RuntimeException {
	public MissingMandatoryFieldException(List<String> fields) {
		super("The following fields are missing: " + fields);
	}
}
