package fortscale.web.rest;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import fortscale.domain.core.User;
import fortscale.domain.core.dao.UserRepository;
import fortscale.services.UserService;
import fortscale.web.beans.UserDetailsBean;
import fortscale.web.beans.UserSearchBean;


@Controller
@RequestMapping("/api/user_details/**")
public class ApiUserDetailsController {

	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private UserService userService;
	
//	@RequestMapping(value="/dn={dn}", method=RequestMethod.GET)
//	@ResponseBody
//	public List<AdUser> user(@PathVariable String dn, Model model){
//		List<AdUser> adUsers = adUserRepository.findByDistinguishedNameIgnoreCaseContaining(dn);
//		return adUsers;
//	}
	
	@RequestMapping(value="{id}", method=RequestMethod.GET)
	@ResponseBody
	public UserDetailsBean user(@PathVariable String id, Model model){
		User user = userRepository.findOne(id);
		if(user == null){
			return null;
		}
		UserDetailsBean ret = new UserDetailsBean(user, getManager(user));
		return ret;
	}
	
	@RequestMapping(value="/search/{prefix}", method=RequestMethod.GET)
	@ResponseBody
	public List<UserSearchBean> search(@PathVariable String prefix, Model model){
		List<User> users = userService.findBySearchFieldContaining(prefix);
		List<UserSearchBean> ret = new ArrayList<UserSearchBean>();
		for(User user: users){
			ret.add(new UserSearchBean(user));
		}
//		UserDetailsListBean ret = new UserDetailsListBean(users);
		return ret;
	}
	
	private User getManager(User user){
		User manager = null;
		if(user.getManagerDN() != null && user.getManagerDN().length() > 0){
			manager = userRepository.findByAdDn(user.getManagerDN());
		}
		return manager;
	}
}
