package fortscale.web.rest;

import java.util.ArrayList;
import java.util.List;

import javax.validation.Valid;

import org.apache.commons.httpclient.auth.InvalidCredentialsException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import fortscale.domain.analyst.Analyst;
import fortscale.domain.analyst.AnalystAuth;
import fortscale.services.analyst.AnalystService;
import fortscale.services.security.MongoUserDetailsService;
import fortscale.utils.logging.annotation.LogException;
import fortscale.web.BaseController;
import fortscale.web.beans.AnalystBean;
import fortscale.web.beans.DataBean;
import fortscale.web.beans.request.RenewPasswordRequest;
import fortscale.web.fields.FirstName;
import fortscale.web.fields.LastName;
import fortscale.web.fields.Password;
import fortscale.web.fields.Username;


@Controller
@RequestMapping("/api/admin/analyst/**")
public class ApiAdminAnalystController extends BaseController{
	
	@Autowired
	private MongoUserDetailsService mongoUserDetailsService;
	@Autowired
	private AnalystService analystService;

	@RequestMapping(value="/addAnalyst", method=RequestMethod.POST)
	@LogException
	public void addAnalyst(@Valid Username username,
			@Valid Password password,
			@Valid FirstName firstName,
			@Valid LastName lastName,
			BindingResult result,
			Model model) throws  Exception{
		if (result.hasErrors()) {
			throw new Exception(result.toString());
		}
		
		mongoUserDetailsService.create(username.toString(), password.toString(), username.toString(), firstName.toString(), lastName.toString());
	}
	
	@RequestMapping(value="/renewPassword", method=RequestMethod.POST)
	@LogException
	public void renewPassword(@RequestBody RenewPasswordRequest renewPasswordRequest) throws InvalidCredentialsException{
		AnalystAuth analystAuth = getThisAnalystAuth();
		//getting analyst auth with credential.
		mongoUserDetailsService.validatePassword(analystAuth.getUsername(), renewPasswordRequest.getPassword());
		mongoUserDetailsService.changePassword(renewPasswordRequest.getUsername(), renewPasswordRequest.getNewPassword(), false);		
	}
	
	@RequestMapping(value="/disableAnalyst", method=RequestMethod.POST)
	@LogException
	public void disableAnalyst(@Valid Username username, BindingResult result, Model model){
		if (result.hasErrors()) {
			throw new RuntimeException(result.toString());
		}
		
		mongoUserDetailsService.disableUser(username.toString());
	}
	
	@RequestMapping(value="/enableAnalyst", method=RequestMethod.POST)
	@LogException
	public void enableAnalyst(@Valid Username username, BindingResult result, Model model){
		if (result.hasErrors()) {
			throw new RuntimeException(result.toString());
		}
		
		mongoUserDetailsService.enableUser(username.toString());
	}
	
	@RequestMapping(value="/details", method=RequestMethod.GET)
	@ResponseBody
	@LogException
	public DataBean<List<AnalystBean>> details(@RequestParam(defaultValue="true") Boolean onlyEnabled, Model model){
		DataBean<List<AnalystBean>> ret = new DataBean<List<AnalystBean>>();
		List<Analyst> analysts = onlyEnabled ? analystService.findAllNonDisabledUsers() : analystService.findAll();
		List<AnalystBean> analystBeans = new ArrayList<>();
		for(Analyst analyst: analysts) {
			UserDetails userDetails = mongoUserDetailsService.loadUserByUsername(analyst.getUserName());
			analystBeans.add(new AnalystBean(analyst, userDetails));
		}
		ret.setData(analystBeans);
		ret.setTotal(analystBeans.size());
		return ret;
	}
}
