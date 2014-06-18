package fortscale.web.rest;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
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
import fortscale.domain.core.AdUserDirectReport;
import fortscale.domain.core.User;
import fortscale.domain.core.dao.UserRepository;
import fortscale.domain.fe.IFeature;
import fortscale.services.IUserScore;
import fortscale.services.IUserScoreHistoryElement;
import fortscale.services.UserServiceFacade;
import fortscale.services.fe.Classifier;
import fortscale.utils.logging.Logger;
import fortscale.utils.logging.annotation.LogException;
import fortscale.web.BaseController;
import fortscale.web.beans.DataBean;
import fortscale.web.beans.DataListWrapperBean;
import fortscale.web.beans.FeatureBean;
import fortscale.web.beans.UserContactInfoBean;
import fortscale.web.beans.UserDetailsBean;
import fortscale.web.beans.UserMachinesBean;
import fortscale.web.beans.UserSearchBean;

@Controller
@RequestMapping("/api/user/**")
public class ApiUserController extends BaseController{
	private static Logger logger = Logger.getLogger(ApiUserController.class);

	@Autowired
	private UserServiceFacade userServiceFacade;
	
	@Autowired
	private UserRepository userRepository;
	
	@RequestMapping(value="/updateAdInfo", method=RequestMethod.GET)
	public void updateAdInfo(@RequestParam(required=false) Long timestampepoch, Model model){
		if(timestampepoch != null){
			userServiceFacade.updateUserWithADInfo(timestampepoch);
		} else{
			userServiceFacade.updateUserWithCurrentADInfo();
		}
	}
	
	@RequestMapping(value="/updateAuthScore", method=RequestMethod.GET)
	public void updateAuthScore(Model model){
		userServiceFacade.updateUserWithAuthScore(Classifier.auth);
	}
	
	@RequestMapping(value="/updateSshScore", method=RequestMethod.GET)
	public void updateSshScore(Model model){
		userServiceFacade.updateUserWithAuthScore(Classifier.ssh);
	}
	
	@RequestMapping(value="/updateVpnScore", method=RequestMethod.GET)
	public void updateVpnScore(Model model){
		userServiceFacade.updateUserWithVpnScore();
	}
	
	@RequestMapping(value="/updateGroupsScore", method=RequestMethod.GET)
	public void updateGroupsScore(Model model){
		userServiceFacade.updateUserWithGroupMembershipScore();
	}
		
	@RequestMapping(value="/recalculateTotalScores", method=RequestMethod.GET)
	public void recalculateTotalScores(Model model){
		userServiceFacade.recalculateTotalScore();
	}
	
	@RequestMapping(value="/search", method=RequestMethod.GET)
	@ResponseBody
	@LogException
	public  DataBean<List<UserSearchBean>> search(@RequestParam(required=true) String prefix,
			@RequestParam(defaultValue="0") Integer page,
			@RequestParam(defaultValue="10") Integer size
			, Model model){
		List<User> users = userServiceFacade.findBySearchFieldContaining(prefix, page, size);
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
		
		Set<String> userRelatedDnsSet = new HashSet<>();
		Map<String, User> dnToUserMap = new HashMap<String, User>();

		fillUserRelatedDns(user, userRelatedDnsSet);
		fillDnToUsersMap(userRelatedDnsSet, dnToUserMap);
		
		UserDetailsBean ret = createUserDetailsBean(user, dnToUserMap, true);
		return new DataListWrapperBean<UserDetailsBean>(ret);
	}
	
	private UserDetailsBean createUserDetailsBean(User user, Map<String, User> dnToUserMap, boolean isWithThumbnail){
		User manager = getUserManager(user, dnToUserMap);
		List<User> directReports = getUserDirectReports(user, dnToUserMap);
		UserDetailsBean ret =  new UserDetailsBean(user, manager, directReports);
		if(isWithThumbnail){
			ret.setThumbnailPhoto(userServiceFacade.getUserThumbnail(user));
		}
		return ret;
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
	
	private User getUserManager(User user, Map<String, User> dnToUserMap){
		User manager = null;
		if(!StringUtils.isEmpty(user.getAdInfo().getManagerDN())){
			manager = dnToUserMap.get(user.getAdInfo().getManagerDN());
		}
		return manager;
	}
	
	private List<User> getUserDirectReports(User user, Map<String, User> dnToUserMap){
		Set<AdUserDirectReport> adUserDirectReports = user.getAdInfo().getDirectReports();
		if(adUserDirectReports == null || adUserDirectReports.isEmpty()){
			return Collections.emptyList();
		}
		
		List<User> directReports = new ArrayList<>();
		for(AdUserDirectReport adUserDirectReport: adUserDirectReports){
			User directReport = dnToUserMap.get(adUserDirectReport.getDn());
			if(directReport != null){
				directReports.add(directReport);
			} else{
				logger.warn("the ad user with the dn ({}) does not exist in the collection user.", adUserDirectReport.getDn());
			}
			
		}
		return directReports;
	}
	
	private void fillUserRelatedDns(User user, Set<String> userRelatedDnsSet){
		if(!StringUtils.isEmpty(user.getAdInfo().getManagerDN())){
			userRelatedDnsSet.add(user.getAdInfo().getManagerDN());
		}
		
		Set<AdUserDirectReport> adUserDirectReports = user.getAdInfo().getDirectReports();
		if(adUserDirectReports != null){
			for(AdUserDirectReport adUserDirectReport: adUserDirectReports){
				userRelatedDnsSet.add(adUserDirectReport.getDn());
			}
		}
	}
	
	private void fillDnToUsersMap(Set<String> userRelatedDnsSet, Map<String, User> dnToUserMap){
		if(userRelatedDnsSet.size() > 0){
			List<User> managers = userRepository.findByDNs(userRelatedDnsSet);
			for(User manager: managers){
				dnToUserMap.put(manager.getAdInfo().getDn(), manager);
			}
		}
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
		
		Set<String> userRelatedDnsSet = new HashSet<>();
		Map<String, User> dnToUserMap = new HashMap<String, User>();
		for(User user: users){
			fillUserRelatedDns(user, userRelatedDnsSet);
		}
		fillDnToUsersMap(userRelatedDnsSet, dnToUserMap);
		
		for(User user: users){
			UserDetailsBean userDetailsBean = createUserDetailsBean(user, dnToUserMap, false);
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
	public DataBean<List<UserMachine>> userMachines(@PathVariable String id, Model model){
		
		List<UserMachine> userMachines = userServiceFacade.getUserMachines(id);
		
		DataBean<List<UserMachine>> ret = new DataBean<List<UserMachine>>();
		ret.setData(userMachines);
		ret.setTotal(userMachines.size());
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
			List<UserMachine> userMachines = userServiceFacade.getUserMachines(user.getId());
			
			usersMachinesList.add(new UserMachinesBean(user.getId(), userMachines));
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
		List<IUserScore> userScores = userServiceFacade.getUserScores(id);
		ret.setData(userScores);
		ret.setTotal(userScores.size());
		return ret;
	}
	
	@RequestMapping(value="/followedUsersScores", method=RequestMethod.GET)
	@ResponseBody
	@LogException
	public DataBean<Map<String, List<IUserScore>>> followedUsersScores(Model model){
		DataBean<Map<String, List<IUserScore>>> ret = new DataBean<Map<String, List<IUserScore>>>();
		Map<User, List<IUserScore>> userScores = userServiceFacade.getFollowedUsersScores();
		Map<String, List<IUserScore>> data = new HashMap<>();
		for(Entry<User, List<IUserScore>> entry: userScores.entrySet()){
			data.put(entry.getKey().getId(), entry.getValue());
		}
		ret.setData(data);
		ret.setTotal(data.size());
		return ret;
	}
	
	@RequestMapping(value="/{uid}/classifier/{classifierId}/scorehistory", method=RequestMethod.GET)
	@ResponseBody
	@LogException
	public DataBean<List<IUserScoreHistoryElement>> userClassifierScoreHistory(@PathVariable String uid, @PathVariable String classifierId,
			@RequestParam(defaultValue="10") Integer limit,
			@RequestParam(defaultValue="0") Integer tzShift,
			Model model){
		DataBean<List<IUserScoreHistoryElement>> ret = new DataBean<List<IUserScoreHistoryElement>>();
		List<IUserScoreHistoryElement> userScores = new ArrayList<>();
		int millisOffset = tzShift * 60 * 1000;
		DateTimeZone dateTimeZone = DateTimeZone.forOffsetMillis(millisOffset);
		DateTime dateLimit = DateTime.now(dateTimeZone);
		dateLimit = dateLimit.withTimeAtStartOfDay();
		dateLimit = dateLimit.minusDays(limit-1);
		DateTime prevElementStartDay = null;
		for(IUserScoreHistoryElement element: userServiceFacade.getUserScoresHistory(uid, classifierId, 0, limit+1)){
			DateTime curElementStartDay = new DateTime(element.getDate().getTime(), dateTimeZone);
			curElementStartDay = curElementStartDay.withTimeAtStartOfDay();
			if(prevElementStartDay != null && curElementStartDay.isEqual(prevElementStartDay.getMillis())){
				continue;
			}
			if(dateLimit.isAfter(element.getDate().getTime())){
				break;
			}
			userScores.add(element);
			if(userScores.size() == limit){
				break;
			}
			prevElementStartDay = curElementStartDay;
		}
		
		Collections.reverse(userScores);
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
			@RequestParam(defaultValue="0") Integer page,
			@RequestParam(defaultValue="-1") Integer size,
			@RequestParam(defaultValue="DESC") String orderByDirection,
			Model model){
		DataBean<List<FeatureBean>> ret = new DataBean<List<FeatureBean>>();
		Direction direction = convertStringToDirection(orderByDirection);
		List<IFeature> attrs = userServiceFacade.getUserAttributesScores(uid, classifierId, Long.parseLong(date), orderBy, direction);
		List<FeatureBean> features = getFeatureBeanList(attrs, page, size);
		ret.setData(features);
		ret.setTotal(attrs.size());
		return ret;
	}
	
	@RequestMapping(value="/classifier/{classifierId}/followedUsersAttributes", method=RequestMethod.GET)
	@ResponseBody
	@LogException
	public DataBean<Map<String, List<FeatureBean>>> followedUsersClassifierAttributes(@PathVariable String classifierId,
			@RequestParam(required=true) String date,
			@RequestParam(required=false) String orderBy,
			@RequestParam(defaultValue="0") Integer attributesPage,
			@RequestParam(defaultValue="-1") Integer attributesSize,
			@RequestParam(defaultValue="DESC") String orderByDirection,
			Model model){
		DataBean<Map<String, List<FeatureBean>>> ret = new DataBean<Map<String, List<FeatureBean>>>();
		Direction direction = convertStringToDirection(orderByDirection);
		Map<User,List<IFeature>> userToAttrsMap = userServiceFacade.getFollowedUserAttributesScores(classifierId, Long.parseLong(date), orderBy, direction);
		Map<String, List<FeatureBean>> data = new HashMap<>();
		for(Entry<User, List<IFeature>> entry: userToAttrsMap.entrySet()){
			List<IFeature> attrs = entry.getValue();
			List<FeatureBean> features = getFeatureBeanList(attrs, attributesPage, attributesSize);
			data.put(entry.getKey().getId(), features);
		}
		ret.setData(data);
		ret.setTotal(data.size());
		return ret;
	}
	
	private List<FeatureBean> getFeatureBeanList(List<IFeature> attrs, int page, int size){
		List<FeatureBean> features = new ArrayList<FeatureBean>();
		if(size > 0){
			int fromIndex = page * size;
			if(fromIndex >= attrs.size()){
				attrs = Collections.emptyList();
			} else{
				int toIndex = fromIndex + size;
				if(toIndex > attrs.size()){
					toIndex = attrs.size();
				}
				attrs = attrs.subList(fromIndex, toIndex);
			}
		}
		for(IFeature feature: attrs){
			features.add(new FeatureBean(feature));
		}
		return features;
	}
	
	@RequestMapping(value="/{uid}/classifier/total/explanation", method=RequestMethod.GET)
	@ResponseBody
	@LogException
	public DataBean<List<IUserScore>> userTotalScoreExplanation(@PathVariable String uid,
			@RequestParam(required=true) String date, Model model){
		DataBean<List<IUserScore>> ret = new DataBean<List<IUserScore>>();
		List<IUserScore> userScores = userServiceFacade.getUserScoresByDay(uid, Long.parseLong(date));
		
		ret.setData(userScores);
		ret.setTotal(userScores.size());
		return ret;
	}
	
	@RequestMapping(value="/removeClassifier", method=RequestMethod.GET)
	@LogException
	public void removeClassifierFromAllUsers(@RequestParam(required=true) String classifierId, Model model){
		userServiceFacade.removeClassifierFromAllUsers(classifierId);
	}	
}
