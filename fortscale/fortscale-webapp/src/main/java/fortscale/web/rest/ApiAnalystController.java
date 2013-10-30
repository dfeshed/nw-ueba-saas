package fortscale.web.rest;

import java.util.List;

import javax.validation.Valid;

import org.apache.commons.httpclient.auth.InvalidCredentialsException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import fortscale.domain.analyst.Analyst;
import fortscale.domain.analyst.AnalystAuth;
import fortscale.services.analyst.AnalystService;
import fortscale.services.exceptions.InvalidValueException;
import fortscale.services.security.MongoUserDetailsService;
import fortscale.utils.logging.annotation.LogException;
import fortscale.web.BaseController;
import fortscale.web.beans.AnalystBean;
import fortscale.web.beans.DataBean;
import fortscale.web.beans.DataListWrapperBean;
import fortscale.web.fields.Password;
import fortscale.web.fields.Username;






@Controller
@RequestMapping("/api/analyst/**")
public class ApiAnalystController extends BaseController{
	
	@Autowired
	private MongoUserDetailsService mongoUserDetailsService;
	@Autowired
	private AnalystService analystService;

//	@RequestMapping(value="signup", method=RequestMethod.POST)
//	@ResponseBody
//	@LogException
//	public String signup(@RequestParam(required=true) String username,
//			@RequestParam(required=true) String password,
//			@RequestParam(required=true) String firstName,
//			@RequestParam(required=true) String lastName,
//			Model model){
//		String ret = "";
//		if(mongoUserDetailsService.userExists(username)) {
//			ret = "User already exist.";
//		} else {
//			try {
//				mongoUserDetailsService.create(username, password, username, firstName, lastName);
//			} catch (Exception e) {
//				//TODO: log
//				ret = e.getMessage();
//			}
//			
//		}
//		
//		return ret;
//	}
	
	@RequestMapping(value="/changePassword", method=RequestMethod.POST)
	@LogException
	public void changePassword(@Valid Username username,
			@Valid Password password,
			@Valid Password newPassword,
			BindingResult result,
			Model model) throws InvalidCredentialsException{
		if (result.hasErrors()) {
			throw new InvalidValueException(result.toString());
		}
		mongoUserDetailsService.changePassword(username.toString(), password.toString(), newPassword.toString());
//		String ret = "";
//		if(!mongoUserDetailsService.userExists(username)) {
//			ret = "User does not exist.";
//		} else {
//			try {
//				
//			} catch (Exception e) {
//				//TODO: log
//				ret = e.getMessage();
//			}
//			
//		}
//		
//		return ret;
	}
	
	@RequestMapping(value="/update", method=RequestMethod.POST)
	@ResponseBody
	@LogException
	public String update(@RequestParam(required=true) String password,
			@RequestParam(required=false) String username,
			@RequestParam(required=false) String firstName,
			@RequestParam(required=false) String lastName,
			@RequestParam(required=false) String newPassword,
			Model model){
		String ret = "";
		AnalystAuth analystAuth = getThisAnalystAuth();
		if(!analystAuth.getPassword().equals(mongoUserDetailsService.encodePassword(password))) {
			ret = "Wrong password";
		} else {
			try {
				mongoUserDetailsService.updateUser(analystAuth.getUsername(), username, newPassword, username, firstName, lastName);
			} catch (Exception e) {
				//TODO: log
				ret = e.getMessage();
			}
			
		}
		
		return ret;
	}
	
	@RequestMapping(value="/{id}/details", method=RequestMethod.GET)
	@ResponseBody
	@LogException
	public DataBean<List<AnalystBean>> details(@PathVariable String id, Model model){
		AnalystAuth analystAuth = getAnalystAuth(id);
		if(analystAuth != null) {
			Analyst analyst = analystService.findByUsername(analystAuth.getUsername());
			if(analyst != null) {
				return new DataListWrapperBean<AnalystBean>(new AnalystBean(analyst) );
			}
		}
		return null;
	}
	
	
}
