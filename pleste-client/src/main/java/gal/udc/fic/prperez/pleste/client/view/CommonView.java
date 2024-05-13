package gal.udc.fic.prperez.pleste.client.view;

import jakarta.servlet.http.HttpSession;
import org.openapitools.client.model.TemplateField.TypeEnum;
import org.springframework.ui.Model;

import java.util.HashMap;

public class CommonView {
	public CommonView() {}

	public static void setModel(Model model, HttpSession session) {
		model.addAttribute("header_Username", session.getAttribute("username"));

		HashMap<TypeEnum, String> fieldTypes = new HashMap<>();
		fieldTypes.put(TypeEnum.LINK, "Ligazón");
		fieldTypes.put(TypeEnum.DATETIME, "Data");
		fieldTypes.put(TypeEnum.TEXT, "Texto libre");
		model.addAttribute("field_enum_map", fieldTypes);
	}
}
