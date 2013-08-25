package fortscale.web.rest;

import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import fortscale.domain.ad.AdUser;
import fortscale.domain.ad.dao.AdUserRepository;


@Controller
@RequestMapping("/api/user_details/**")
public class ApiUserController {

	@Autowired
	private AdUserRepository adUserRepository;
	
//	@RequestMapping(value="/dn={dn}", method=RequestMethod.GET)
//	@ResponseBody
//	public List<AdUser> user(@PathVariable String dn, Model model){
//		List<AdUser> adUsers = adUserRepository.findByDistinguishedNameIgnoreCaseContaining(dn);
//		return adUsers;
//	}
	
	@RequestMapping(value="{id}", method=RequestMethod.GET)
	@ResponseBody
	public AdUser user(@PathVariable ObjectId id, Model model){
		AdUser adUser = adUserRepository.findOne(id);
		return adUser;
	}
}
