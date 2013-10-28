package fortscale.web.rest;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import fortscale.domain.analyst.Analyst;
import fortscale.services.analyst.AnalystService;
import fortscale.services.security.MongoUserDetailsService;
import fortscale.utils.logging.annotation.LogException;
import fortscale.web.beans.AnalystBean;
import fortscale.web.beans.DataBean;


@Controller
@RequestMapping("/api/admin/analyst/**")
public class ApiAdminAnalystController {
	
	@Autowired
	private MongoUserDetailsService mongoUserDetailsService;
	@Autowired
	private AnalystService analystService;

	@RequestMapping(value="/addAnalyst", method=RequestMethod.POST)
	@ResponseBody
	@LogException
	public String addAnalyst(@RequestParam(required=true) String username,
			@RequestParam(required=true) String password,
			@RequestParam(required=true) String firstName,
			@RequestParam(required=true) String lastName,
			Model model){
		String ret = "";
		if(mongoUserDetailsService.userExists(username)) {
			ret = "User already exist.";
		} else {
			try {
				mongoUserDetailsService.create(username, password, username, firstName, lastName);
			} catch (Exception e) {
				//TODO: log
				ret = e.getMessage();
			}
			
		}
		
		return ret;
	}
	
	@RequestMapping(value="/disableAnalyst", method=RequestMethod.POST)
	@ResponseBody
	@LogException
	public String disableAnalyst(@RequestParam(required=true) String username, Model model){
		String ret = "";
		if(!mongoUserDetailsService.userExists(username)) {
			ret = "User does not exist.";
		} else {
			try {
				mongoUserDetailsService.disableUser(username);
			} catch (Exception e) {
				//TODO: log
				ret = e.getMessage();
			}
			
		}
		
		return ret;
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
