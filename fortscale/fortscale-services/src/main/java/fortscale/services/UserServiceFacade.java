package fortscale.services;

import java.util.List;
import java.util.Map;

import org.joda.time.DateTime;
import org.springframework.data.domain.Sort.Direction;

import fortscale.domain.ad.AdUser;
import fortscale.domain.ad.UserMachine;
import fortscale.domain.core.User;
import fortscale.domain.events.LogEventsEnum;
import fortscale.domain.fe.IFeature;
import fortscale.services.fe.Classifier;
import fortscale.services.types.PropertiesDistribution;


public interface UserServiceFacade {
	public void updateUserWithCurrentADInfo();
	
	public void updateUserWithADInfo(Long timestampepoch);
	
	public List<User> findBySearchFieldContaining(String prefix, int page, int size);
	
	public List<IUserScore> getUserScores(String uid); 
	
	public List<IUserScore> getUserScoresByDay(String uid, Long dayTimestamp); 
	
	public List<IFeature> getUserAttributesScores(String uid, String classifierId, Long timestamp, String orderBy, Direction direction, Integer minScore);
	
	public Map<User,List<IFeature>> getFollowedUserAttributesScores(String classifierId, Long timestamp, String orderBy, Direction direction); 
	
	public List<IUserScoreHistoryElement> getUserScoresHistory(String uid, String classifierId, DateTime fromDate, DateTime toDate);
	
	public List<UserMachine> getUserMachines(String uid);
	
	public List<User> findByApplicationUserName(UserApplication userApplication, List<String> usernames);
	
	public String findByNormalizedUserName(String normalizedUsername);
	
	public void recalculateTotalScore();
		
	public void removeClassifierFromAllUsers(String classifierId);

	public Map<User, List<IUserScore>> getUsersScoresByIds(List<String> uids);
	
	public Map<User, List<IUserScore>> getFollowedUsersScores();

	public List<String> getFollowedUsersVpnLogUsername();

	public List<String> getFollowedUsersAuthLogUsername(LogEventsEnum eventId);
	
	public String getUserThumbnail(User user);

	public void updateUserWithADInfo(AdUser adUser);

	public void updateUserTotalScore();
	
	public void updateOrCreateUserWithClassifierUsername(Classifier classifier, String normalizedUsername, String logUsername, boolean onlyUpdate, boolean updateAppUsername);
	
	public PropertiesDistribution getDestinationComputerPropertyDistribution(String uid, String propertyName, int daysToGet, int maxValues, int minScore);
}
