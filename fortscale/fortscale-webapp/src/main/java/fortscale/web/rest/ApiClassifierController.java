package fortscale.web.rest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import fortscale.services.UserService;





@Controller
@RequestMapping("/api/classifier/**")
public class ApiClassifierController {

	@Autowired
	private UserService userService;
	
	
	
}
