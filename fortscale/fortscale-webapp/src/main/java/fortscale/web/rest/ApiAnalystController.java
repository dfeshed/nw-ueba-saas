package fortscale.web.rest;

import fortscale.common.exceptions.InvalidValueException;
import fortscale.domain.analyst.Analyst;
import fortscale.domain.analyst.AnalystAuth;
import fortscale.domain.analyst.AnalystSavedSearch;
import fortscale.services.analyst.AnalystService;
import fortscale.services.analyst.ConfigurationService;
import fortscale.services.security.MongoUserDetailsService;
import fortscale.utils.logging.annotation.HideSensitiveArgumentsFromLog;
import fortscale.utils.logging.annotation.LogException;
import fortscale.web.BaseController;
import fortscale.web.beans.AnalystBean;
import fortscale.web.beans.DataBean;
import fortscale.web.beans.DataListWrapperBean;
import fortscale.web.fields.NewPassword;
import fortscale.web.fields.Password;
import fortscale.web.fields.Username;
import org.apache.commons.httpclient.auth.InvalidCredentialsException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.validation.Valid;
import java.util.List;






@Controller
@RequestMapping("/api/analyst/**")
public class ApiAnalystController extends BaseController{
	
	@Autowired
	private MongoUserDetailsService mongoUserDetailsService;
	@Autowired
	private AnalystService analystService;
	@Autowired
	private ConfigurationService configurationService;

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
//				need to log
//				ret = e.getMessage();
//			}
//			
//		}
//		
//		return ret;
//	}
	
	@RequestMapping(value="/changePassword", method=RequestMethod.POST)
	@LogException
	@HideSensitiveArgumentsFromLog//Don't print users passwords to the log
	public void changePassword(@Valid Username username,
			@Valid Password password,
			@Valid NewPassword newPassword,
			BindingResult result,
			Model model) throws InvalidCredentialsException{
		if (result.hasErrors()) {
			throw new InvalidValueException(result.toString());
		}
		mongoUserDetailsService.changePassword(username.toString(), password.toString(), newPassword.toString());
	}
	
	@RequestMapping(value="/update", method=RequestMethod.POST)
	@LogException
	@HideSensitiveArgumentsFromLog//Don't print users passwords to the log
	public void update(@RequestParam(required=true) String password,
			@RequestParam(required=false) String username,
			@RequestParam(required=false) String firstName,
			@RequestParam(required=false) String lastName,
			@RequestParam(required=false) String newPassword,
			Model model) throws InvalidCredentialsException{
		AnalystAuth analystAuth = getThisAnalystAuth();
		//getting analyst auth with credential.
		analystAuth = mongoUserDetailsService.getAnalystAuthByUsernameAndPassword(analystAuth.getUsername(), password);
		mongoUserDetailsService.updateUser(analystAuth.getUsername(), username, newPassword, username, firstName, lastName);		
	}
	
	@RequestMapping(value="/updateScoreDistribution", method=RequestMethod.GET)
	@LogException
	public void updateScoreDistribution(@RequestParam(required=true) String dist, Model model){
		configurationService.setScoreDistribution(dist);
	}
	
	@RequestMapping(value="/me/details", method=RequestMethod.GET)
	@ResponseBody
	@LogException
	public DataBean<List<AnalystBean>> details(Model model){
		AnalystAuth analystAuth = getThisAnalystAuth();
		if(analystAuth != null) {
			Analyst analyst = analystService.findByUsername(analystAuth.getUsername());
			if(analyst != null) {
				return new DataListWrapperBean<AnalystBean>(new AnalystBean(analyst, analystAuth) );
			}
		}
		return null;
	}
	
	
	@RequestMapping(value="/followUser", method=RequestMethod.GET)
	@LogException
	public void followUser(@RequestParam(required=true) String userId,
			@RequestParam(defaultValue="true") Boolean follow,
			Model model){
		AnalystAuth analystAuth = getThisAnalystAuth();
		if(analystAuth != null) {
			analystService.followUser(analystAuth, userId, follow);
		}
	}
	
	@RequestMapping(value="/savedSearch/create", method=RequestMethod.POST)
	@LogException
	public DataBean<String> createSavedSearch(@RequestParam(required=true) String name,
			@RequestParam(required=true) String category,
			@RequestParam(required=false) String description,
			@RequestParam(required=true) String filter,
			Model model){
		AnalystAuth analystAuth = getThisAnalystAuth();
		if(analystAuth == null) {
			return null;
		}
		String id = analystService.createSavedSearch(analystAuth, name, category, filter, description);
		
		DataBean<String> ret = new DataBean<>();
		ret.setData(id);
		
		return ret;
	}
	
	@RequestMapping(value="/savedSearch/find", method=RequestMethod.GET)
	@LogException
	public DataBean<List<AnalystSavedSearch>> findSavedSearch(@RequestParam(required=false) String id,
			Model model){
		AnalystAuth analystAuth = getThisAnalystAuth();
		if(analystAuth == null) {
			return null;
		}
		List<AnalystSavedSearch> analystSavedSearchs = analystService.findSavedSearch(id);
		
		DataBean<List<AnalystSavedSearch>> ret = new DataBean<>();
		ret.setData(analystSavedSearchs);
		ret.setTotal(analystSavedSearchs.size());
		
		return ret;
	}	
}
