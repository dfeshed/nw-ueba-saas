package fortscale.web.rest;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import fortscale.activedirectory.main.ADManager;






@Controller
@RequestMapping("/api/analyst/**")
public class ApiAnalystController {

	@RequestMapping(value="/addDefUser", method=RequestMethod.GET)
	@ResponseBody
	public String runfe(Model model){
		ADManager adManager = new ADManager();
		adManager.run(feService, null);
		return "";
//		List<AdUser> ret = IteratorUtils.toList(feService.getAdUsersAttrVals().iterator());
//		return ret;
	}
}
