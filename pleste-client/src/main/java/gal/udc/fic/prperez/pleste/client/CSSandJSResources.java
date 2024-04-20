package gal.udc.fic.prperez.pleste.client;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import java.io.IOException;
import java.io.InputStream;

@Controller
public class CSSandJSResources {

	private String openFile(String type, String url) {
		String filename = url.substring(url.lastIndexOf("/") + 1);

		ClassLoader classloader = Thread.currentThread().getContextClassLoader();
		try (InputStream is = classloader.getResourceAsStream("templates/" + type + "/" + filename)) {
			byte[] filedata = is.readAllBytes();

			return new String(filedata);
		} catch (IOException e) {
			return null;
		}
	}

	@GetMapping("/css/**")
	public ResponseEntity<String> returnCSS(HttpServletRequest req) {
		String file = openFile("css", req.getRequestURI());
		if (file == null) {
			return ResponseEntity.notFound().build();
		} else {
			return ResponseEntity.ok().contentType(new MediaType("text", "css")).body(file);
		}
	}

	@GetMapping("/js/**")
	public ResponseEntity<String> returnJS(HttpServletRequest req) {
		String file = openFile("js", req.getRequestURI());
		if (file == null) {
			return ResponseEntity.notFound().build();
		} else {
			return ResponseEntity.ok().contentType(new MediaType("text", "javascript")).body(file);
		}
	}
}
