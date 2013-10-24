package fortscale.web.rest;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import fortscale.domain.ad.UserMachine;
import fortscale.domain.core.User;
import fortscale.domain.core.dao.UserRepository;
import fortscale.domain.fe.IFeature;
import fortscale.services.IUserScore;
import fortscale.services.IUserScoreHistoryElement;
import fortscale.services.UserService;
import fortscale.utils.logging.annotation.LogException;
import fortscale.web.beans.DataBean;
import fortscale.web.beans.DataListWrapperBean;
import fortscale.web.beans.FeatureBean;
import fortscale.web.beans.UserContactInfoBean;
import fortscale.web.beans.UserDetailsBean;
import fortscale.web.beans.UserMachineBean;
import fortscale.web.beans.UserMachinesBean;
import fortscale.web.beans.UserSearchBean;

@Controller
@RequestMapping("/api/user/**")
public class ApiUserController {

	@Autowired
	private UserService userService;
	
	@Autowired
	private UserRepository userRepository;
	
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
	
	@RequestMapping(value="/updateAuthScore", method=RequestMethod.GET)
	@ResponseBody
	public String updateAuthScore(Model model){
		userService.updateUserWithAuthScore();
		return "";
	}
	
	@RequestMapping(value="/updateVpnScore", method=RequestMethod.GET)
	@ResponseBody
	public String updateVpnScore(Model model){
		userService.updateUserWithVpnScore();
		return "";
	}
	
	@RequestMapping(value="/search", method=RequestMethod.GET)
	@ResponseBody
	@LogException
	public  DataBean<List<UserSearchBean>> search(@RequestParam(required=true) String prefix, Model model){
		List<User> users = userService.findBySearchFieldContaining(prefix);
		List<UserSearchBean> data = new ArrayList<UserSearchBean>();
		for(User user: users){
			data.add(new UserSearchBean(user));
		}
//		UserDetailsListBean ret = new UserDetailsListBean(users);
		DataBean<List<UserSearchBean>> ret = new DataBean<List<UserSearchBean>>();
		ret.setData(data);
		ret.setTotal(data.size());
		return ret;
	}
	
	@RequestMapping(value="{id}/details", method=RequestMethod.GET)
	@ResponseBody
	@LogException
	public DataBean<List<UserDetailsBean>> details(@PathVariable String id, Model model){
		User user = userRepository.findOne(id);
		if(user == null){
			return null;
		}
		UserDetailsBean ret = new UserDetailsBean(user, getManager(user));
		return new DataListWrapperBean<UserDetailsBean>(ret);
	}
	
	@RequestMapping(value="usersDetails", method=RequestMethod.GET)
	@ResponseBody
	@LogException
	public DataBean<List<UserDetailsBean>> usersDetails(@RequestParam(required=true) List<String> ids, Model model){
		List<UserDetailsBean> userDetailsBeans = new ArrayList<>();
		for(String id: ids) {
			User user = userRepository.findOne(id);
			if(user == null){
				return null;
			}
			UserDetailsBean userDetailsBean = new UserDetailsBean(user, getManager(user));
			userDetailsBeans.add(userDetailsBean);
		}
		DataBean<List<UserDetailsBean>> ret = new DataBean<>();
		ret.setData(userDetailsBeans);
		ret.setTotal(userDetailsBeans.size());
		return ret;
	}
	
	@RequestMapping(value="{id}/contactinfo", method=RequestMethod.GET)
	@ResponseBody
	@LogException
	public DataBean<List<UserContactInfoBean>> userContactInfo(@PathVariable String id, Model model){
		User user = userRepository.findOne(id);
		if(user == null){
			return null;
		}
		UserContactInfoBean ret = new UserContactInfoBean(user);
		return new DataListWrapperBean<UserContactInfoBean>(ret);
	}
	
	@RequestMapping(value="{id}/machines", method=RequestMethod.GET)
	@ResponseBody
	@LogException
	public DataBean<List<UserMachineBean>> userMachines(@PathVariable String id, Model model){
		DataBean<List<UserMachineBean>> ret = new DataBean<List<UserMachineBean>>();
		List<UserMachine> userMachines = userService.getUserMachines(id);
		
		List<UserMachineBean> userMachinesBean = new ArrayList<UserMachineBean>();
		for(UserMachine userMachine: userMachines){
			userMachinesBean.add(new UserMachineBean(userMachine));
		}
		ret.setData(userMachinesBean);
		ret.setTotal(userMachinesBean.size());
		return ret;
	}
	
	@RequestMapping(value="usersMachines", method=RequestMethod.GET)
	@ResponseBody
	@LogException
	public DataBean<List<UserMachinesBean>> usersMachines(@RequestParam(required=true) List<String> ids, Model model){
		List<UserMachinesBean> usersMachinesList = new ArrayList<>();
		for(String id: ids) {
			List<UserMachine> userMachines = userService.getUserMachines(id);
			
			List<UserMachineBean> userMachinesBean = new ArrayList<UserMachineBean>();
			for(UserMachine userMachine: userMachines){
				userMachinesBean.add(new UserMachineBean(userMachine));
			}
			usersMachinesList.add(new UserMachinesBean(id, userMachinesBean));
		}
		DataBean<List<UserMachinesBean>> ret = new DataBean<>();
		ret.setData(usersMachinesList);
		ret.setTotal(usersMachinesList.size());
		return ret;
	}
	
	@RequestMapping(value="{id}/scores", method=RequestMethod.GET)
	@ResponseBody
	@LogException
	public DataBean<List<IUserScore>> userScores(@PathVariable String id, Model model){
		DataBean<List<IUserScore>> ret = new DataBean<List<IUserScore>>();
		List<IUserScore> userScores = userService.getUserScores(id);
		ret.setData(userScores);
		ret.setTotal(userScores.size());
		return ret;
	}
	
	@RequestMapping(value="{uid}/classifier/{classifierId}/scorehistory", method=RequestMethod.GET)
	@ResponseBody
	@LogException
	public DataBean<List<IUserScoreHistoryElement>> userClassifierScoreHistory(@PathVariable String uid, @PathVariable String classifierId,
			@RequestParam(defaultValue="0") Integer offset,
			@RequestParam(defaultValue="7") Integer limit,
			Model model){
		DataBean<List<IUserScoreHistoryElement>> ret = new DataBean<List<IUserScoreHistoryElement>>();
		List<IUserScoreHistoryElement> userScores = userService.getUserScoresHistory(uid, classifierId, offset, limit);
		ret.setData(userScores);
		ret.setTotal(userScores.size());
		return ret;
	}
	
	@RequestMapping(value="{uid}/classifier/{classifierId}/attributes", method=RequestMethod.GET)
	@ResponseBody
	@LogException
	public DataBean<List<FeatureBean>> userClassifierAttributes(@PathVariable String uid, @PathVariable String classifierId,
			@RequestParam(required=true) String date, Model model){
		DataBean<List<FeatureBean>> ret = new DataBean<List<FeatureBean>>();
		List<IFeature> attrs = userService.getUserAttributesScores(uid, classifierId, new Date(Long.parseLong(date)));
		List<FeatureBean> features = new ArrayList<FeatureBean>();
		for(IFeature feature: attrs){
			features.add(new FeatureBean(feature));
		}
		ret.setData(features);
		ret.setTotal(features.size());
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
