package fortscale.web.rest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import fortscale.services.UserService;

@Controller
@RequestMapping("/api/user/**")
public class ApiUserController {

	@Autowired
	private UserService userService;
	
	@RequestMapping(value="/updateAdInfo", method=RequestMethod.GET)
	@ResponseBody
	public String updateAdInfo(@RequestParam(required=false) String timestamp, Model model){
		if(timestamp != null){
			userService.updateUserWithADInfo(timestamp);
		} else{
			userService.updateUserWithCurrentADInfo();
		}
		return "";
	}
}
