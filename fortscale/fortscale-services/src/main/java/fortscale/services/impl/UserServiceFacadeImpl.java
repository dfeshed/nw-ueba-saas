package fortscale.services.impl;

import fortscale.domain.ad.AdUser;
import fortscale.domain.ad.UserMachine;
import fortscale.domain.core.User;
import fortscale.services.*;
import fortscale.services.types.PropertiesDistribution;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Set;

@Service("userServiceFacade")
public class UserServiceFacadeImpl implements UserServiceFacade{
	
	@Autowired
	private UserService userService;
//	@Autowired
//	private UserUpdateScoreService userUpdateScoreService;
//	@Autowired
//	private UserScoreService userScoreService;
	@Autowired
	private UsernameService usernameService;

	@Override
	public List<User> findBySearchFieldContaining(String prefix, int page, int size) {
		return userService.findBySearchFieldContaining(prefix, page, size);
	}
	
	@Override
	public List<UserMachine> getUserMachines(String uid) {
		return userService.getUserMachines(uid);
	}
	
	@Override
	public List<User> findByApplicationUserName(UserApplication userApplication, List<String> usernames) {
		return userService.findByApplicationUserName(userApplication, usernames);
	}
	
	@Override
	public String getUserThumbnail(User user) {
		return userService.getUserThumbnail(user);
	}

	@Override
	public void updateUserWithADInfo(AdUser adUser) {
		userService.updateUserWithADInfo(adUser);
	}

	@Override
	public PropertiesDistribution getDestinationComputerPropertyDistribution(String uid, String propertyName, Long latestDate, Long earliestDate, int maxValues, int minScore) {
		return userService.getDestinationComputerPropertyDistribution(uid, propertyName, latestDate,earliestDate, maxValues, minScore);
	}

	@Override public Boolean isPasswordExpired(User user) {
		return userService.isPasswordExpired(user);
	}

	@Override public Boolean isNoPasswordRequiresValue(User user) {
		return userService.isNoPasswordRequiresValue(user);
	}

	@Override public Boolean isNormalUserAccountValue(User user) {
		return userService.isNormalUserAccountValue(user);
	}

	@Override public Boolean isPasswordNeverExpiresValue(User user) {
		return userService.isPasswordNeverExpiresValue(user);
	}

	@Override public String getOu(User user) {
		return userService.getOu(user);
	}

	@Override public void fillUserRelatedDns(User user, Set<String> userRelatedDnsSet) {
		userService.fillUserRelatedDns(user, userRelatedDnsSet);
	}

	@Override public void fillDnToUsersMap(Set<String> userRelatedDnsSet, Map<String, User> dnToUserMap) {
		userService.fillDnToUsersMap(userRelatedDnsSet,dnToUserMap);
	}

	@Override public User getUserManager(User user, Map<String, User> dnToUserMap) {
		return userService.getUserManager(user,dnToUserMap);
	}

	@Override public List<User> getUserDirectReports(User user, Map<String, User> dnToUserMap) {
		return userService.getUserDirectReports(user, dnToUserMap);
	}

	@Override
	public String findByNormalizedUserName(String normalizedUsername) {
		return userService.findByNormalizedUserName(normalizedUsername);
	}
}
