package fortscale.web.rest;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort.Direction;
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
import fortscale.services.fe.Classifier;
import fortscale.utils.logging.annotation.LogException;
import fortscale.web.BaseController;
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
public class ApiUserController extends BaseController{

	@Autowired
	private UserService userService;
	
	@Autowired
	private UserRepository userRepository;
	
	@RequestMapping(value="/updateAdInfo", method=RequestMethod.GET)
	public void updateAdInfo(@RequestParam(required=false) String timestamp, Model model){
		if(timestamp != null){
			userService.updateUserWithADInfo(timestamp);
		} else{
			userService.updateUserWithCurrentADInfo();
		}
	}
	
	@RequestMapping(value="/updateAuthScore", method=RequestMethod.GET)
	public void updateAuthScore(Model model){
		userService.updateUserWithAuthScore(Classifier.auth);
	}
	
	@RequestMapping(value="/updateSshScore", method=RequestMethod.GET)
	public void updateSshScore(Model model){
		userService.updateUserWithAuthScore(Classifier.ssh);
	}
	
	@RequestMapping(value="/updateVpnScore", method=RequestMethod.GET)
	public void updateVpnScore(Model model){
		userService.updateUserWithVpnScore();
	}
	
	@RequestMapping(value="/updateGroupsScore", method=RequestMethod.GET)
	public void updateGroupsScore(Model model){
		userService.updateUserWithGroupMembershipScore();
	}
	
	@RequestMapping(value="/recalculateScores", method=RequestMethod.GET)
	public void recalculateScores(Model model){
		userService.recalculateUsersScores();
	}
	
	@RequestMapping(value="/recalculateTotalScores", method=RequestMethod.GET)
	public void recalculateTotalScores(Model model){
		userService.recalculateTotalScore();
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
	
	@RequestMapping(value="/{id}/details", method=RequestMethod.GET)
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
	
	@RequestMapping(value="/followedUsers", method=RequestMethod.GET)
	@ResponseBody
	@LogException
	public DataBean<List<String>> followedUsers(Model model){
		List<String> userIds = new ArrayList<>();
		for(User user: userRepository.findByFollowed(true)){
			userIds.add(user.getId());
		}
		
		DataBean<List<String>> ret = new DataBean<>();
		ret.setData(userIds);
		ret.setTotal(userIds.size());
		return ret;
	}
	
	@RequestMapping(value="/usersDetails", method=RequestMethod.GET)
	@ResponseBody
	@LogException
	public DataBean<List<UserDetailsBean>> usersDetails(@RequestParam(required=true) List<String> ids, Model model){
		List<User> users = userRepository.findByIds(ids);
		return userDetails(users);
	}
	
	@RequestMapping(value="/followedUsersDetails", method=RequestMethod.GET)
	@ResponseBody
	@LogException
	public DataBean<List<UserDetailsBean>> followedUsersDetails(Model model){
		List<User> users = userRepository.findByFollowed(true);
		return userDetails(users);
	}
	
	private DataBean<List<UserDetailsBean>> userDetails(List<User> users){
		List<UserDetailsBean> userDetailsBeans = new ArrayList<>();
		
		Map<String, User> dnToUserMap = new HashMap<String, User>(users.size());
		for(User user: users){
			if(!StringUtils.isEmpty(user.getManagerDN())){
				dnToUserMap.put(user.getManagerDN(), null);
			}
		}
		List<User> managers = userRepository.findByDNs(dnToUserMap.keySet());
		for(User manager: managers){
			dnToUserMap.put(manager.getAdDn(), manager);
		}
		for(User user: users){
			User manager = null;
			if(!StringUtils.isEmpty(user.getManagerDN())){
				manager = dnToUserMap.get(user.getManagerDN());
			}
			UserDetailsBean userDetailsBean = new UserDetailsBean(user, manager);
			userDetailsBeans.add(userDetailsBean);
		}
		DataBean<List<UserDetailsBean>> ret = new DataBean<>();
		ret.setData(userDetailsBeans);
		ret.setTotal(userDetailsBeans.size());
		return ret;
	}
	
	@RequestMapping(value="/{id}/contactinfo", method=RequestMethod.GET)
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
	
	@RequestMapping(value="/{id}/machines", method=RequestMethod.GET)
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
	
	@RequestMapping(value="/usersMachines", method=RequestMethod.GET)
	@ResponseBody
	@LogException
	public DataBean<List<UserMachinesBean>> usersMachines(@RequestParam(required=true) List<String> ids, Model model){
		List<User> users = userRepository.findByIds(ids);
		return usersMachines(users);
	}
	
	@RequestMapping(value="/followedUsersMachines", method=RequestMethod.GET)
	@ResponseBody
	@LogException
	public DataBean<List<UserMachinesBean>> followedUsersMachines(Model model){
		List<User> users = userRepository.findByFollowed(true);
		return usersMachines(users);
	}
	
	private DataBean<List<UserMachinesBean>> usersMachines(List<User> users){
		List<UserMachinesBean> usersMachinesList = new ArrayList<>();
		for(User user: users) {
			List<UserMachine> userMachines = userService.getUserMachines(user.getId());
			
			List<UserMachineBean> userMachinesBean = new ArrayList<UserMachineBean>();
			for(UserMachine userMachine: userMachines){
				userMachinesBean.add(new UserMachineBean(userMachine));
			}
			usersMachinesList.add(new UserMachinesBean(user.getId(), userMachinesBean));
		}
		DataBean<List<UserMachinesBean>> ret = new DataBean<>();
		ret.setData(usersMachinesList);
		ret.setTotal(usersMachinesList.size());
		return ret;
	}
	
	@RequestMapping(value="/{id}/scores", method=RequestMethod.GET)
	@ResponseBody
	@LogException
	public DataBean<List<IUserScore>> userScores(@PathVariable String id, Model model){
		DataBean<List<IUserScore>> ret = new DataBean<List<IUserScore>>();
		List<IUserScore> userScores = userService.getUserScores(id);
		ret.setData(userScores);
		ret.setTotal(userScores.size());
		return ret;
	}
	
	@RequestMapping(value="/followedUsersScores", method=RequestMethod.GET)
	@ResponseBody
	@LogException
	public DataBean<Map<String, List<IUserScore>>> followedUsersScores(Model model){
		DataBean<Map<String, List<IUserScore>>> ret = new DataBean<Map<String, List<IUserScore>>>();
		Map<String, List<IUserScore>> userScores = userService.getFollowedUsersScores();
		ret.setData(userScores);
		ret.setTotal(userScores.size());
		return ret;
	}
	
	@RequestMapping(value="/{uid}/classifier/{classifierId}/scorehistory", method=RequestMethod.GET)
	@ResponseBody
	@LogException
	public DataBean<List<IUserScoreHistoryElement>> userClassifierScoreHistory(@PathVariable String uid, @PathVariable String classifierId,
			@RequestParam(defaultValue="0") Integer offset,
			@RequestParam(defaultValue="7") Integer limit,
			Model model){
		DataBean<List<IUserScoreHistoryElement>> ret = new DataBean<List<IUserScoreHistoryElement>>();
		List<IUserScoreHistoryElement> userScores = new ArrayList<>();
		Calendar calendar = Calendar.getInstance();
		calendar.add(Calendar.DAY_OF_MONTH, -(limit-1));
		for(IUserScoreHistoryElement element: userService.getUserScoresHistory(uid, classifierId, offset, limit)){
			if(calendar.getTime().after(element.getDate())){
				continue;
			}
			userScores.add(element);
		}
		ret.setData(userScores);
		ret.setTotal(userScores.size());
		return ret;
	}
	
	@RequestMapping(value="/{uid}/classifier/{classifierId}/attributes", method=RequestMethod.GET)
	@ResponseBody
	@LogException
	public DataBean<List<FeatureBean>> userClassifierAttributes(@PathVariable String uid, @PathVariable String classifierId,
			@RequestParam(required=true) String date,
			@RequestParam(required=false) String orderBy,
			@RequestParam(defaultValue="DESC") String orderByDirection,
			Model model){
		DataBean<List<FeatureBean>> ret = new DataBean<List<FeatureBean>>();
		Direction direction = convertStringToDirection(orderByDirection);
		List<IFeature> attrs = userService.getUserAttributesScores(uid, classifierId, Long.parseLong(date), orderBy, direction);
		List<FeatureBean> features = new ArrayList<FeatureBean>();
		for(IFeature feature: attrs){
			features.add(new FeatureBean(feature));
		}
		ret.setData(features);
		ret.setTotal(features.size());
		return ret;
	}
	
	@RequestMapping(value="/{uid}/classifier/total/explanation", method=RequestMethod.GET)
	@ResponseBody
	@LogException
	public DataBean<List<IUserScore>> userTotalScoreExplanation(@PathVariable String uid,
			@RequestParam(required=true) String date, Model model){
		DataBean<List<IUserScore>> ret = new DataBean<List<IUserScore>>();
		List<IUserScore> userScores = userService.getUserScoresByDay(uid, Long.parseLong(date));
		
		ret.setData(userScores);
		ret.setTotal(userScores.size());
		return ret;
	}
	
	@RequestMapping(value="/removeClassifier", method=RequestMethod.GET)
	@LogException
	public void removeClassifierFromAllUsers(@RequestParam(required=true) String classifierId, Model model){
		userService.removeClassifierFromAllUsers(classifierId);
	}
	
	private User getManager(User user){
		User manager = null;
		if(user.getManagerDN() != null && user.getManagerDN().length() > 0){
			manager = userRepository.findByAdDn(user.getManagerDN());
		}
		return manager;
	}
	
	
}
