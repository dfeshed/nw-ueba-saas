package fortscale.web.rest;

import java.util.List;

import org.apache.commons.collections.IteratorUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import fortscale.domain.ad.AdUser;
import fortscale.services.fe.FeService;




@Controller
@RequestMapping("/api/aduser/**")
public class ApiAdUserController {
	
	@Autowired
	private FeService feService;

	@SuppressWarnings("unchecked")
	@RequestMapping(value="/runfe", method=RequestMethod.GET)
	@ResponseBody
	public List<AdUser> runfe(Model model){
		return IteratorUtils.toList(feService.getAdUsersAttrVals().iterator());
	}
}
