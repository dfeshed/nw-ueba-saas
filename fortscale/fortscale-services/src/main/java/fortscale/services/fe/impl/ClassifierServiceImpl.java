package fortscale.services.fe.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.IntRange;
import org.apache.commons.lang.math.Range;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.jdbc.core.JdbcOperations;
import org.springframework.stereotype.Service;

import fortscale.domain.core.ClassifierScore;
import fortscale.domain.core.User;
import fortscale.domain.core.dao.UserRepository;
import fortscale.domain.events.LogEventsEnum;
import fortscale.domain.fe.EventScore;
import fortscale.domain.fe.dao.AccessDAO;
import fortscale.domain.fe.dao.AdUsersFeaturesExtractionRepository;
import fortscale.domain.fe.dao.EventLoginDayCount;
import fortscale.domain.fe.dao.EventResultRepository;
import fortscale.domain.fe.dao.Threshold;
import fortscale.services.UserService;
import fortscale.services.analyst.ConfigurationService;
import fortscale.services.exceptions.UnknownResourceException;
import fortscale.services.fe.Classifier;
import fortscale.services.fe.ClassifierService;
import fortscale.services.fe.IClassifierScoreDistribution;
import fortscale.services.fe.IScoreDistribution;
import fortscale.services.fe.ISuspiciousUserInfo;
import fortscale.services.impl.SeverityElement;
import fortscale.services.impl.UsernameService;
import fortscale.utils.impala.ImpalaPageRequest;
import fortscale.utils.logging.Logger;

@Service("classifierService")
public class ClassifierServiceImpl implements ClassifierService{
	private static Logger logger = Logger.getLogger(ClassifierServiceImpl.class);
		
	
	
	@Autowired
	private AdUsersFeaturesExtractionRepository adUsersFeaturesExtractionRepository;
	
	@Autowired
	private JdbcOperations impalaJdbcTemplate;
	
	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private EventResultRepository eventResultRepository;
	
	@Autowired
	private AccessDAO loginDAO;
	
	@Autowired
	private AccessDAO sshDAO;
	
	@Autowired
	private AccessDAO vpnDAO;
	
	@Autowired
	private UserService userService;
	
	@Autowired
	private ConfigurationService configurationService;
		
	@Autowired
	private UsernameService usernameService;	
	
	
	

	

	public Classifier getClassifier(String classifierId){
		return configurationService.getClassifiersMap().get(classifierId);
	}
	
	public List<IClassifierScoreDistribution> getScoreDistribution(){
		List<IClassifierScoreDistribution> ret = new ArrayList<>();
		for(Classifier classifier: Classifier.values()){
			String classifierId = classifier.getId();
			List<IScoreDistribution> dists = getScoreDistribution(classifierId);
			ret.add(new ClassifierScoreDistribution(classifierId, dists));
		}
		return ret;
	}

	@Override
	public List<IScoreDistribution> getScoreDistribution(String classifierId) {
		Classifier.validateClassifierId(classifierId);
		
		List<Threshold> thresholds = new ArrayList<>();
		thresholds.add(new Threshold("All", 0));
		for(SeverityElement element: configurationService.getSeverityElements()){
			thresholds.add(new Threshold(element.getName(), element.getValue()));
		}
		calculateNumOfUsersWithScoresGTThresholdForLastRun(classifierId, thresholds);
		
		if(thresholds.get(0).getCount() == 0){
			return Collections.emptyList();
		}
		
		List<IScoreDistribution> ret = new ArrayList<>();
		int total = thresholds.get(0).getCount();
		int prevPercent = 0;
		int prevCount = 0;
		int i = 0;
		int prevThreshold = 100;
		for(Threshold threshold: thresholds){
			if(i == 0){
				i++;
				continue;
			}
			int percent = (int)Math.round(((threshold.getCount()/(double)total)*100));
			int count = threshold.getCount() - prevCount;
			ret.add(new ScoreDistribution(threshold.getName(), count, percent - prevPercent,threshold.getValue(), prevThreshold));
			prevPercent = percent;
			prevCount = threshold.getCount();
			prevThreshold = threshold.getValue();
		}
		return ret;
	}
	
	private void calculateNumOfUsersWithScoresGTThresholdForLastRun(String classifierId,List<Threshold> thresholds){
		for(Threshold threshold: thresholds){
			threshold.setCount(userRepository.countNumOfUsersAboveThreshold(classifierId, threshold));
		}
	}
	
	@Override
	public int countUsers(String classifierId){
		return userRepository.countNumOfUsers(classifierId);
	}
	
	@Override
	public Page<ISuspiciousUserInfo> getSuspiciousUsersByScore(String classifierId, String severityId, int page, int size, boolean followedOnly) {
		Range severityRange = getRange(severityId);
		return getTopUsers(classifierId, new ThresholdNoFilter(),page,size, severityRange.getMinimumInteger(), severityRange.getMaximumInteger(), followedOnly, User.getClassifierScoreCurrentScoreField(classifierId), User.getClassifierScoreCurrentTrendScoreField(classifierId));
	}
	
	@Override
	public Page<ISuspiciousUserInfo> getSuspiciousUsersByTrend(String classifierId, String severityId, int page, int size, boolean followedOnly) {
		Range severityRange = getRange(severityId);
		return getTopUsers(classifierId, new ThresholdTrendFilter(), page, size, severityRange.getMinimumInteger(), severityRange.getMaximumInteger(), followedOnly, User.getClassifierScoreCurrentTrendScoreField(classifierId), User.getClassifierScoreCurrentScoreField(classifierId));
	}
	
	@Override
	public Page<ISuspiciousUserInfo> getSuspiciousUsersByScore(String classifierId, int page, int size, Integer minScore, Integer maxScore, boolean followedOnly) {
		return getTopUsers(classifierId, new ThresholdNoFilter(),page,size, minScore, maxScore, followedOnly, User.getClassifierScoreCurrentScoreField(classifierId), User.getClassifierScoreCurrentTrendScoreField(classifierId));
	}
	
	@Override
	public Page<ISuspiciousUserInfo> getSuspiciousUsersByTrend(String classifierId, int page, int size, Integer minScore, Integer maxScore, boolean followedOnly) {
		return getTopUsers(classifierId, new ThresholdTrendFilter(),page,size, minScore, maxScore, followedOnly, User.getClassifierScoreCurrentTrendScoreField(classifierId), User.getClassifierScoreCurrentScoreField(classifierId));
	}

	private Page<ISuspiciousUserInfo> getTopUsers(String classifierId, ThresholdFilter thresholdFilter, int page, int size, Integer minScore, Integer maxScore, boolean followedOnly, String... sortingFieldsName) {
		Classifier.validateClassifierId(classifierId);
		
		Pageable pageable = new PageRequest(page, size, Direction.DESC, sortingFieldsName);
		Page<User> users = null;
		DateTime dateTime = new DateTime();
		dateTime = dateTime.minusHours(24);
			
		if(followedOnly){
			users = userRepository.findByClassifierIdAndFollowedAndScoreBetweenAndTimeGteAsData(classifierId, minScore, maxScore, dateTime.toDate(), pageable);
		} else{
			users = userRepository.findByClassifierIdAndScoreBetweenAndTimeGteAsData(classifierId, minScore, maxScore, dateTime.toDate(), pageable);
		}
		
		List<ISuspiciousUserInfo> retList = new ArrayList<>();
		for(User user: users.getContent()){
			ISuspiciousUserInfo suspiciousUserInfo = createSuspiciousUserInfo(classifierId, user);
			if(!thresholdFilter.hasPassed(suspiciousUserInfo)){
				break;
			}
			retList.add(suspiciousUserInfo);
		}
				
		return new PageImpl<>(retList, pageable, users.getTotalElements());		
	}
		
	private SuspiciousUserInfo createSuspiciousUserInfo(String classifierId, User user){
		ClassifierScore classifierScore = user.getScore(classifierId);
		int trendSign = classifierScore.getTrend() > 0 ? 1 : -1;
		return new SuspiciousUserInfo(user.getId(), user.getUsername(), (int) Math.floor(user.getScore(classifierId).getScore()), Math.round(classifierScore.getTrendScore()*trendSign), user.getFollowed());
	}
	
	private Range getRange(String severityId){
		if(severityId == null){
			return new IntRange(0, 101);
		}
		return configurationService.getRange(severityId);
	}
	
	@Override
	public int countAuthEvents(LogEventsEnum eventId, String userId){
		AccessDAO accessDAO = getAccessDAO(eventId);
		User user = userRepository.findOne(userId);
		if(user == null){
			throw new UnknownResourceException(String.format("user with id [%s] does not exist", userId));
		}
		String logUsername = usernameService.getAuthLogUsername(eventId, user);
		if(logUsername != null){
			return accessDAO.countNumOfEventsByNormalizedUsername(user.getUsername());
		} else{
			return 0;
		}
	}
	
	
	@Override
	public List<EventScore> getEventScores(List<LogEventsEnum> classifierIds, String username, int daysBack, int limit) {
		
		List<EventScore> eventScores = new LinkedList<EventScore>();
		for (LogEventsEnum  classifierId : classifierIds) {
			AccessDAO accessDAO = getAccessDAO(classifierId);
			eventScores.addAll(accessDAO.getEventScores(username, daysBack, limit));
		}
		return eventScores;
	}
	
	@Override
	public List<EventLoginDayCount> getEventLoginDayCount(LogEventsEnum eventId, String username, int numberOfDays){
		AccessDAO accessDAO = getAccessDAO(eventId);
		return accessDAO.getEventLoginDayCount(username, numberOfDays);
	}
	
	@Override
	public int countAuthEvents(LogEventsEnum eventId, Long latestDate, Long earliestDate, String userId, int minScore){
		AccessDAO accessDAO = getAccessDAO(eventId);
		User user = userRepository.findOne(userId);
		if(user == null){
			throw new UnknownResourceException(String.format("user with id [%s] does not exist", userId));
		}
		String logUsername = usernameService.getAuthLogUsername(eventId, user);
		if(logUsername != null){
			return accessDAO.countNumOfEventsByNormalizedUsernameAndGtEScoreAndBetweenTimes(user.getUsername(), minScore, latestDate, earliestDate);
		} else{
			return 0;
		}
	}
	
	@Override
	public int countAuthEvents(LogEventsEnum eventId, Long latestDate, Long earliestDate, int minScore, boolean onlyFollowedUsers){
		AccessDAO accessDAO = getAccessDAO(eventId);

		List<String> usernames = null;
		if(onlyFollowedUsers){
			usernames = usernameService.getFollowedUsersUsername(eventId);
			if(usernames.isEmpty()){
				return 0;
			}
		}
		return accessDAO.countNumOfEventsByGTEScoreAndBetweenTimesAndNormalizedUsernameList(minScore, latestDate, earliestDate, usernames);
	}

	@Override
	public List<Map<String, Object>> getUserSuspiciousAuthEvents(LogEventsEnum eventId, Long latestDate, Long earliestDate, String userId, int offset, int limit, String orderBy, Direction direction, int minScore) {
		AccessDAO accessDAO = getAccessDAO(eventId);
		
		User user = userRepository.findOne(userId);
		if(user == null){
			throw new UnknownResourceException(String.format("user with id [%s] does not exist", userId));
		}
		String logUsername = user.getLogUsernameMap().get(accessDAO.getTableName());
		if(StringUtils.isEmpty(logUsername)){
			return Collections.emptyList();
		}
		String orderByArray[] = processAuthScoreOrderByFieldName(accessDAO, orderBy);
		Pageable pageable = new ImpalaPageRequest(offset + limit, new Sort(direction, orderByArray));
		List<Map<String, Object>> authScores = accessDAO.findEventsByNormalizedUsernameAndGtEventScoreAndBetweenTimes(user.getUsername(), minScore, latestDate, earliestDate, pageable);
		List<Map<String, Object>> ret = new ArrayList<>();
		if(offset < authScores.size()){
			for(Map<String, Object> row: authScores.subList(offset, authScores.size())){
				addUserInfoToAuthEventScoreInfo(user, row);
				ret.add(row);
			}
		}
		return ret;
	}
	
	@Override
	public int countAuthEvents(LogEventsEnum eventId){
		AccessDAO accessDAO = getAccessDAO(eventId);
		return accessDAO.countNumOfEvents();
	}

	@Override
	public List<Map<String, Object>> getSuspiciousAuthEvents(LogEventsEnum eventId, Long latestDate, Long earliestDate, int offset, int limit, String orderBy, Direction direction, Integer minScore, boolean onlyFollowedUsers) {
		AccessDAO accessDAO = getAccessDAO(eventId);
		String orderByArray[] = processAuthScoreOrderByFieldName(accessDAO, orderBy);
		
		List<String> usernames = null;
		if(onlyFollowedUsers){
			usernames = usernameService.getFollowedUsersUsername(eventId);
			if(usernames.isEmpty()){
				return Collections.emptyList();
			}
		}

		Pageable pageable = new ImpalaPageRequest(offset + limit, new Sort(direction, orderByArray));
		List<Map<String, Object>> results = accessDAO.findEventsByGtEventScoreBetweenTimeInUsernameList(pageable, minScore, latestDate, earliestDate, usernames);
		List<Map<String, Object>> ret = new ArrayList<>();
		if(offset < results.size()){
			Map<String, User> userMap = new HashMap<>();
			for(Map<String, Object> row: results.subList(offset, results.size())){
				String username = (String) row.get(accessDAO.getNormalizedUsernameField());
				User user = userMap.get(username);
				if(user == null){
					user = userRepository.findByUsername(username);
					if(user == null){
						logger.warn("username ({}) was not found in the user collection", username);
						continue;
					}
					
					userMap.put(username, user);
				}
				addUserInfoToAuthEventScoreInfo(user, row);
				ret.add(row);
			}
		}
		return ret;
	}
	
	public AccessDAO getAccessDAO(LogEventsEnum eventId){
		AccessDAO ret = null;
		switch(eventId){
			case login:
				ret = loginDAO;
				break;
			case ssh:
				ret = sshDAO;
				break;
			case vpn:
				ret = vpnDAO;
				break;
		default:
			break;
		}
		
		return ret;
	}
	
	private String[] processAuthScoreOrderByFieldName(AccessDAO accessDAO, String orderBy){
		if(StringUtils.isEmpty(orderBy)){
			orderBy = accessDAO.getEventScoreFieldName();
		}
		
		String ret[];
		if(!orderBy.equals(accessDAO.getEventTimeFieldName())){
			ret = new String[2];
			ret[0] = orderBy;
			ret[1] = accessDAO.getEventTimeFieldName();
		} else{
			ret = new String[1];
			ret[0] = orderBy;
		}
		
		return ret;
	}
	

	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	private void addUserInfoToAuthEventScoreInfo(User user, Map<String, Object> authEventScoreInfoMap){
		authEventScoreInfoMap.put("userId", user.getId());
		authEventScoreInfoMap.put("username", user.getUsername());
		authEventScoreInfoMap.put("isUserFollowed", user.getFollowed());
	}
	
	
	
	
	
	

	interface ThresholdFilter{
		public boolean hasPassed(ISuspiciousUserInfo suspiciousUserInfo);
	}
	
	class ThresholdNoFilter implements ThresholdFilter{

		@Override
		public boolean hasPassed(ISuspiciousUserInfo suspiciousUserInfo) {
			return true;
		}
		
	}
	
	class ThresholdTrendFilter implements ThresholdFilter{

		@Override
		public boolean hasPassed(ISuspiciousUserInfo suspiciousUserInfo) {
			return true;//suspiciousUserInfo.getTrend() > 0;
		}
		
	}
}
