package fortscale.services;

import fortscale.domain.ad.AdUser;
import fortscale.domain.ad.UserMachine;
import fortscale.domain.core.User;
import fortscale.services.types.PropertiesDistribution;

import java.util.List;
import java.util.Map;
import java.util.Set;


public interface UserServiceFacade {
	public List<User> findBySearchFieldContaining(String prefix, int page, int size);
	
	//public List<IUserScore> getUserScores(String uid);
	
	//public List<IUserScore> getUserScoresByDay(String uid, Long dayTimestamp);

	//public List<IUserScoreHistoryElement> getUserScoresHistory(String uid, String classifierId, long fromEpochTime, long toEpochTime, int tzShift);
	
	public List<UserMachine> getUserMachines(String uid);
	
	public List<User> findByApplicationUserName(UserApplication userApplication, List<String> usernames);
	
	public String findByNormalizedUserName(String normalizedUsername);
	
	//public void recalculateTotalScore();
	
	public String getUserThumbnail(User user);

	public void updateUserWithADInfo(AdUser adUser);

//	public void updateUserTotalScore();
	
	public PropertiesDistribution getDestinationComputerPropertyDistribution(String uid, String propertyName, Long latestDate, Long earliestDate, int maxValues, int minScore);

	public Boolean isPasswordExpired(User user);

	public Boolean isNoPasswordRequiresValue(User user);

	public Boolean isNormalUserAccountValue(User user);

	public Boolean isPasswordNeverExpiresValue(User user);

	public String getOu(User user);

	public void fillUserRelatedDns(User user, Set<String> userRelatedDnsSet);

	public void fillDnToUsersMap(Set<String> userRelatedDnsSet, Map<String, User> dnToUserMap);

	public User getUserManager(User user, Map<String, User> dnToUserMap);

	public List<User> getUserDirectReports(User user, Map<String, User> dnToUserMap);
}
