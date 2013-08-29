package fortscale.web.rest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import fortscale.activedirectory.main.ADManager;
import fortscale.services.fe.FeService;




@Controller
@RequestMapping("/api/aduser/**")
public class ApiAdUserController {
	
	@Autowired
	private FeService feService;

//	@SuppressWarnings("unchecked")
	@RequestMapping(value="/runfe", method=RequestMethod.GET)
	@ResponseBody
	public String runfe(Model model){
		ADManager adManager = new ADManager();
		adManager.run(feService, null);
		return "";
//		List<AdUser> ret = IteratorUtils.toList(feService.getAdUsersAttrVals().iterator());
//		return ret;
	}
}
