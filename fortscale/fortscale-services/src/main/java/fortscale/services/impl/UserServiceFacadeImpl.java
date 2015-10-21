package fortscale.services.impl;

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.stereotype.Service;

import fortscale.domain.ad.AdUser;
import fortscale.domain.ad.UserMachine;
import fortscale.domain.core.User;
import fortscale.domain.events.LogEventsEnum;
import fortscale.domain.fe.IFeature;
import fortscale.services.IUserScore;
import fortscale.services.IUserScoreHistoryElement;
import fortscale.services.UserApplication;
import fortscale.services.UserScoreService;
import fortscale.services.UserService;
import fortscale.services.UserServiceFacade;
import fortscale.services.UserUpdateScoreService;
import fortscale.services.fe.Classifier;
import fortscale.services.types.PropertiesDistribution;

@Service("userServiceFacade")
public class UserServiceFacadeImpl implements UserServiceFacade{
	
	@Autowired
	private UserService userService;
	@Autowired
	private UserUpdateScoreService userUpdateScoreService;
	@Autowired
	private UserScoreService userScoreService;
	@Autowired
	private UsernameService usernameService;

	@Override
	public void updateUserWithCurrentADInfo() {
		userService.updateUserWithCurrentADInfo();
	}

	@Override
	public void updateUserWithADInfo(Long timestampepoch) {
		userService.updateUserWithADInfo(timestampepoch);
	}

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
	public void removeClassifierFromAllUsers(String classifierId) {
		userService.removeClassifierFromAllUsers(classifierId);
	}
	
	@Override
	public List<String> getFollowedUsersVpnLogUsername() {
		return usernameService.getFollowedUsersVpnLogUsername();
	}
	
	@Override
	public List<String> getFollowedUsersAuthLogUsername(LogEventsEnum eventId) {
		return usernameService.getFollowedUsersAuthLogUsername(eventId);
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
	public Map<User, List<IUserScore>> getUsersScoresByIds(List<String> uids) {
		return userScoreService.getUsersScoresByIds(uids);
	}

	@Override
	public Map<User, List<IUserScore>> getFollowedUsersScores() {
		return userScoreService.getFollowedUsersScores();
	}

	@Override
	public List<IUserScore> getUserScores(String uid) {
		return userScoreService.getUserScores(uid);
	}

	@Override
	public List<IUserScore> getUserScoresByDay(String uid, Long dayTimestamp) {
		return userScoreService.getUserScoresByDay(uid, dayTimestamp);
	}

	@Override
	public List<IUserScoreHistoryElement> getUserScoresHistory(String uid, String classifierId, long fromEpochTime, long toEpochTime, int tzShift) {
		return userScoreService.getUserScoresHistory(uid, classifierId, fromEpochTime, toEpochTime, tzShift);
	}


	@Override
	public void recalculateTotalScore() {
		userUpdateScoreService.recalculateTotalScore();
	}

	@Override
	public void updateUserTotalScore() {
		userUpdateScoreService.updateUserTotalScore();
	}

	@Override
	public void updateOrCreateUserWithClassifierUsername(Classifier classifier, String normalizedUsername, String logUsername, boolean onlyUpdate, boolean updateAppUsername) {
		userService.updateOrCreateUserWithClassifierUsername(classifier, normalizedUsername, logUsername, onlyUpdate, updateAppUsername);
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
