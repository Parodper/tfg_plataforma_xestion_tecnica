package gal.udc.fic.prperez.pleste.service;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.Embeddable;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeParseException;

/**
 * Custom OffsetDateTime wrapper, thanks to issue <a href="https://github.com/OpenAPITools/openapi-generator/issues/18547">#18547</a>
 */
@Embeddable
public class JSONDatetime  {
	@JsonFormat(shape = JsonFormat.Shape.STRING)
	private OffsetDateTime datetime;

	public JSONDatetime(OffsetDateTime datetime) {
		this.datetime = datetime;
	}

	public JSONDatetime() {
	}

	public OffsetDateTime getDatetime() {
		return datetime;
	}
	public void setDatetime(OffsetDateTime datetime) {
		this.datetime = datetime;
	}

	public static JSONDatetime parse(String datetime) throws DateTimeParseException {
		try {
			return new JSONDatetime(OffsetDateTime.parse(datetime));
		} catch (DateTimeParseException e) {
			LocalDateTime date = LocalDateTime.parse(datetime);
			return new JSONDatetime(date.atZone(ZoneId.systemDefault()).toOffsetDateTime());
		}
	}

	public static JSONDatetime now() {
		return new JSONDatetime(OffsetDateTime.now());
	}
}
