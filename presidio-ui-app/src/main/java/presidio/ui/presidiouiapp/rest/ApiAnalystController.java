package presidio.ui.presidiouiapp.rest;

import fortscale.domain.analyst.Analyst;
import fortscale.domain.analyst.AnalystAuth;
import fortscale.domain.core.EmailAddress;
import fortscale.services.UserService;
import fortscale.services.analyst.AnalystService;

import fortscale.services.security.MongoUserDetailsService;
import fortscale.utils.logging.annotation.HideSensitiveArgumentsFromLog;

import presidio.ui.presidiouiapp.BaseController;
import presidio.ui.presidiouiapp.beans.AnalystBean;
import presidio.ui.presidiouiapp.beans.DataBean;
import presidio.ui.presidiouiapp.beans.DataListWrapperBean;
import presidio.ui.presidiouiapp.fields.NewPassword;
import presidio.ui.presidiouiapp.fields.Password;
import presidio.ui.presidiouiapp.fields.Username;
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
import java.util.ArrayList;
import java.util.List;






@Controller
@RequestMapping("/api/analyst/**")
public class ApiAnalystController extends BaseController{

	public static final String MOCK_ANALYST_USER = "ca-admin";


	
	@RequestMapping(value="/changePassword", method=RequestMethod.POST)
	//@LogException
	@HideSensitiveArgumentsFromLog//Don't print users passwords to the log
	public void changePassword(@Valid Username username,
			@Valid Password password,
			@Valid NewPassword newPassword,
			BindingResult result,
			Model model) throws Exception{
		if (result.hasErrors()) {
			throw new Exception(result.toString());
		}
//		mongoUserDetailsService.changePassword(username.toString(), password.toString(), newPassword.toString());
	}
	
	@RequestMapping(value="/update", method=RequestMethod.POST)
//	@LogException
	@HideSensitiveArgumentsFromLog//Don't print users passwords to the log
	public void update(@RequestParam(required=true) String password,
			@RequestParam(required=false) String username,
			@RequestParam(required=false) String firstName,
			@RequestParam(required=false) String lastName,
			@RequestParam(required=false) String newPassword,
			Model model) throws InvalidCredentialsException{
		AnalystAuth analystAuth = getThisAnalystAuth();
		//getting analyst auth with credential.
//		analystAuth = mongoUserDetailsService.getAnalystAuthByUsernameAndPassword(analystAuth.getUsername(), password);
//		mongoUserDetailsService.updateUser(analystAuth.getUsername(), username, newPassword, username, firstName, lastName);
	}
	
//	@RequestMapping(value="/updateScoreDistribution", method=RequestMethod.GET)
//	@LogException
//	public void updateScoreDistribution(@RequestParam(required=true) String dist, Model model){
//		configurationService.setScoreDistribution(dist);
//	}
	
	@RequestMapping(value="/me/details", method=RequestMethod.GET)
	@ResponseBody
	//@LogException
	public DataBean<List<AnalystBean>> details(Model model){
//		AnalystAuth analystAuth = getThisAnalystAuth();
//		if(analystAuth != null) {
//			Analyst analyst = analystService.findByUsername(analystAuth.getUsername());
//			if(analyst != null) {
//				return new DataListWrapperBean<AnalystBean>(new AnalystBean(analyst, analystAuth) );
//			}
//		}
		//TODO: on presidio we need a way to recive and extrat the analyst details from the kerberos.
		//This is a temp solution:
		AnalystAuth analystAuth = new AnalystAuth(MOCK_ANALYST_USER,"admin",new ArrayList<>());
		Analyst analyst = new Analyst(MOCK_ANALYST_USER,new EmailAddress(MOCK_ANALYST_USER+"@quest.com"),"Admin","CA");
		return new DataListWrapperBean<AnalystBean>(new AnalystBean(analyst, analystAuth) );
	}
	
	

	

}
