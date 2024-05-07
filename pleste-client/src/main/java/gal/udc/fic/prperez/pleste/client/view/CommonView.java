package gal.udc.fic.prperez.pleste.client.view;

import jakarta.servlet.http.HttpSession;
import org.springframework.ui.Model;

public class CommonView {
	public CommonView() {}

	public static void setModel(Model model, HttpSession session) {
		model.addAttribute("user", session.getAttribute("username"));
	}
}
