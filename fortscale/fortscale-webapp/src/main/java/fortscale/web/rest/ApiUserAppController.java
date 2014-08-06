package fortscale.web.rest;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import fortscale.domain.core.User;
import fortscale.services.UserApplication;
import fortscale.services.UserServiceFacade;
import fortscale.utils.logging.annotation.LogException;
import fortscale.web.beans.ApplicationUserDetailsBean;
import fortscale.web.beans.DataBean;
import fortscale.web.beans.UserDetailsBean;
import fortscale.web.beans.UserIdBean;



@Controller
@RequestMapping("/api/app/**")
public class ApiUserAppController {

	@Autowired
	private UserServiceFacade userServiceFacade;
	
	@RequestMapping(value="/{appId}/usernameToId", method=RequestMethod.GET)
	@ResponseBody
	@LogException
	public DataBean<List<ApplicationUserDetailsBean>> usernameToId(@PathVariable String appId,
			@RequestParam(required=true) List<String> usernames, Model model){
		DataBean<List<ApplicationUserDetailsBean>> ret = new DataBean<>();
		UserApplication userApplication = UserApplication.valueOf(appId);
		List<User> users = userServiceFacade.findByApplicationUserName(userApplication, usernames);
		List<ApplicationUserDetailsBean> applicationUserDetailsBeans = new ArrayList<>();
		for(User user: users) {
			applicationUserDetailsBeans.add(new ApplicationUserDetailsBean(user, userApplication));	
		}

		ret.setData(applicationUserDetailsBeans);
		ret.setTotal(applicationUserDetailsBeans.size());
		return ret;
	}
	

	
	@RequestMapping(value="/{appId}/usersDetails", method=RequestMethod.GET)
	@ResponseBody
	@LogException
	public DataBean<List<UserDetailsBean>> usersDetails(@PathVariable String appId,
			@RequestParam(required=true) List<String> usernames, Model model){
		DataBean<List<UserDetailsBean>> ret = new DataBean<>();
		UserApplication userApplication = UserApplication.valueOf(appId);
		List<User> users = userServiceFacade.findByApplicationUserName(userApplication, usernames);
		List<UserDetailsBean> applicationUserDetailsBeans = new ArrayList<>();
		for(User user: users) {
			applicationUserDetailsBeans.add(new UserDetailsBean(user,null, Collections.<User>emptyList()));	
		}

		ret.setData(applicationUserDetailsBeans);
		ret.setTotal(applicationUserDetailsBeans.size());
		return ret;
	}
}
