package fortscale.services.impl;

import java.util.Date;
import java.util.List;
import java.util.Map;

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
	public List<IFeature> getUserAttributesScores(String uid, String classifierId, Long timestamp, String orderBy, Direction direction) {
		return userScoreService.getUserAttributesScores(uid, classifierId, timestamp, orderBy, direction);
	}

	@Override
	public Map<User, List<IFeature>> getFollowedUserAttributesScores(String classifierId, Long timestamp, String orderBy, Direction direction) {
		return userScoreService.getFollowedUserAttributesScores(classifierId, timestamp, orderBy, direction);
	}

	@Override
	public List<IUserScoreHistoryElement> getUserScoresHistory(String uid, String classifierId, int offset, int limit) {
		return userScoreService.getUserScoresHistory(uid, classifierId, offset, limit);
	}

	@Override
	public void updateUserWithAuthScore(Classifier classifier) {
		userUpdateScoreService.updateUserWithAuthScore(classifier);
	}
	
	@Override
	public void updateUserWithAuthScore(Classifier classifier, Date runtime) {
		userUpdateScoreService.updateUserWithAuthScore(classifier, runtime);
	}
	
	@Override
	public void updateUserWithGroupMembershipScore() {
		userUpdateScoreService.updateUserWithGroupMembershipScore();
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
	public PropertiesDistribution getDestinationComputerPropertyDistribution(String uid, String propertyName, int daysToGet, int maxValues, int minScore) {
		return userService.getDestinationComputerPropertyDistribution(uid, propertyName, daysToGet, maxValues, minScore);
	}
}
