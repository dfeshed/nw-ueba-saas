package fortscale.web.rest;

import java.util.ArrayList;
import java.util.List;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import fortscale.domain.analyst.Analyst;
import fortscale.services.analyst.AnalystService;
import fortscale.services.exceptions.InvalidValueException;
import fortscale.services.security.MongoUserDetailsService;
import fortscale.utils.logging.annotation.LogException;
import fortscale.web.beans.AnalystBean;
import fortscale.web.beans.DataBean;
import fortscale.web.fields.FirstName;
import fortscale.web.fields.LastName;
import fortscale.web.fields.Password;
import fortscale.web.fields.Username;


@Controller
@RequestMapping("/api/admin/analyst/**")
public class ApiAdminAnalystController {
	
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
			Model model){
		if (result.hasErrors()) {
			throw new InvalidValueException(result.toString());
		}
		
		mongoUserDetailsService.create(username.toString(), password.toString(), username.toString(), firstName.toString(), lastName.toString());
	}
	
	@RequestMapping(value="/disableAnalyst", method=RequestMethod.POST)
	@LogException
	public void disableAnalyst(@Valid Username username, BindingResult result, Model model){
		if (result.hasErrors()) {
			throw new InvalidValueException(result.toString());
		}
		
		mongoUserDetailsService.disableUser(username.toString());
	}
	
	@RequestMapping(value="/details", method=RequestMethod.GET)
	@ResponseBody
	@LogException
	public DataBean<List<AnalystBean>> details(Model model){
		DataBean<List<AnalystBean>> ret = new DataBean<List<AnalystBean>>();
		List<Analyst> analysts = analystService.findAllNonDisabledUsers();
		List<AnalystBean> analystBeans = new ArrayList<>();
		for(Analyst analyst: analysts) {
			analystBeans.add(new AnalystBean(analyst));
		}
		ret.setData(analystBeans);
		ret.setTotal(analystBeans.size());
		return ret;
	}
}
