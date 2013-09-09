package fortscale.web.rest;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;






@Controller
@RequestMapping("/api/analyst/**")
public class ApiAnalystController {

	@RequestMapping(value="/addDefUser", method=RequestMethod.GET)
	@ResponseBody
	public String addDefUser(Model model){
		
		return "";
	}
}
