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
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.jdbc.core.JdbcOperations;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;

import fortscale.domain.core.ApplicationUserDetails;
import fortscale.domain.core.ClassifierScore;
import fortscale.domain.core.User;
import fortscale.domain.core.dao.UserRepository;
import fortscale.domain.events.LogEventsEnum;
import fortscale.domain.fe.AuthScore;
import fortscale.domain.fe.EventScore;
import fortscale.domain.fe.VpnScore;
import fortscale.domain.fe.dao.AdUsersFeaturesExtractionRepository;
import fortscale.domain.fe.dao.AuthDAO;
import fortscale.domain.fe.dao.EventLoginDayCount;
import fortscale.domain.fe.dao.EventResultRepository;
import fortscale.domain.fe.dao.EventScoreDAO;
import fortscale.domain.fe.dao.Threshold;
import fortscale.domain.fe.dao.VpnDAO;
import fortscale.domain.system.ServersListConfiguration;
import fortscale.services.UserApplication;
import fortscale.services.UserService;
import fortscale.services.analyst.ConfigurationService;
import fortscale.services.exceptions.UnknownResourceException;
import fortscale.services.fe.Classifier;
import fortscale.services.fe.ClassifierService;
import fortscale.services.fe.IClassifierScoreDistribution;
import fortscale.services.fe.ILoginEventScoreInfo;
import fortscale.services.fe.IScoreDistribution;
import fortscale.services.fe.ISuspiciousUserInfo;
import fortscale.services.fe.IVpnEventScoreInfo;
import fortscale.services.impl.SeverityElement;
import fortscale.services.impl.UsernameService;
import fortscale.utils.impala.ImpalaPageRequest;
import fortscale.utils.impala.ImpalaParser;
import fortscale.utils.logging.Logger;

@Service("classifierService")
public class ClassifierServiceImpl implements ClassifierService, InitializingBean{
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
	private AuthDAO loginDAO;
	
	@Autowired
	private AuthDAO sshDAO;
	
	@Autowired
	private VpnDAO vpnDAO;
	
	@Autowired
	private UserService userService;
	
	@Autowired
	private ConfigurationService configurationService;
	
	@Autowired
	private ServersListConfiguration serversListConfiguration;
	
	@Autowired
	private ImpalaParser impalaParser;
	
	@Autowired
	private ThreadPoolTaskExecutor mongoDbWriterExecuter;
	
	@Autowired
	private UsernameService usernameService;
	
	
	
	
	
	private Map<String, Map<String, String>> rowFieldRegexFilter = new HashMap<>();
	
	
	
	

	

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
		EventScoreDAO eventScoreDAO = getEventScoreDAO(eventId);
		User user = userRepository.findOne(userId);
		if(user == null){
			throw new UnknownResourceException(String.format("user with id [%s] does not exist", userId));
		}
		String logUsername = usernameService.getAuthLogUsername(eventId, user);
		if(logUsername != null){
			return eventScoreDAO.countNumOfEventsByNormalizedUsername(user.getUsername());
		} else{
			return 0;
		}
	}
	
	
	@Override
	public List<EventScore> getEventScores(List<LogEventsEnum> classifierIds, String username, int daysBack, int limit) {
		
		List<EventScore> eventScores = new LinkedList<EventScore>();
		for (LogEventsEnum  classifierId : classifierIds) {
			EventScoreDAO eventScoreDAO = getEventScoreDAO(classifierId);
			eventScores.addAll(eventScoreDAO.getEventScores(username, daysBack, limit));
		}
		return eventScores;
	}
	
	@Override
	public List<EventLoginDayCount> getEventLoginDayCount(LogEventsEnum eventId, String username, int numberOfDays){
		EventScoreDAO eventScoreDAO = getEventScoreDAO(eventId);
		return eventScoreDAO.getEventLoginDayCount(username, numberOfDays);
	}
	
	@Override
	public int countAuthEvents(LogEventsEnum eventId, String userId, int minScore){
		EventScoreDAO eventScoreDAO = getEventScoreDAO(eventId);
		User user = userRepository.findOne(userId);
		if(user == null){
			throw new UnknownResourceException(String.format("user with id [%s] does not exist", userId));
		}
		String logUsername = usernameService.getAuthLogUsername(eventId, user);
		if(logUsername != null){
			return eventScoreDAO.countNumOfEventsByNormalizedUsernameAndGtEScore(user.getUsername(), minScore);
		} else{
			return 0;
		}
	}
	
	@Override
	public int countAuthEvents(LogEventsEnum eventId, int minScore, boolean onlyFollowedUsers){
		EventScoreDAO eventScoreDAO = getEventScoreDAO(eventId);

		List<String> usernames = null;
		if(onlyFollowedUsers){
			usernames = usernameService.getFollowedUsersUsername(eventId);
			if(usernames.isEmpty()){
				return 0;
			}
		}
		return eventScoreDAO.countNumOfEventsByGTEScoreAndNormalizedUsernameList(minScore, usernames);
	}

	@Override
	public List<ILoginEventScoreInfo> getUserSuspiciousAuthEvents(LogEventsEnum eventId, String userId, int offset, int limit, String orderBy, Direction direction, int minScore) {
		AuthDAO authDAO = getAuthDAO(eventId);
		
		User user = userRepository.findOne(userId);
		if(user == null){
			throw new UnknownResourceException(String.format("user with id [%s] does not exist", userId));
		}
		String logUsername = user.getLogUsernameMap().get(authDAO.getTableName());
		if(StringUtils.isEmpty(logUsername)){
			return Collections.emptyList();
		}
		String orderByArray[] = processAuthScoreOrderByFieldName(orderBy);
		Pageable pageable = new ImpalaPageRequest(offset + limit, new Sort(direction, orderByArray));
		List<AuthScore> authScores = authDAO.findEventsByNormalizedUsernameAndGtEventScore(user.getUsername(), minScore, pageable);
		List<ILoginEventScoreInfo> ret = new ArrayList<>();
		if(offset < authScores.size()){
			for(AuthScore authScore: authScores.subList(offset, authScores.size())){
				ret.add(createLoginEventScoreInfo(user, authScore));
			}
		}
		return ret;
	}
	
	@Override
	public int countAuthEvents(LogEventsEnum eventId){
		AuthDAO authDAO = getAuthDAO(eventId);
		return authDAO.countNumOfEvents();
	}

	@Override
	public List<ILoginEventScoreInfo> getSuspiciousAuthEvents(LogEventsEnum eventId, int offset, int limit, String orderBy, Direction direction, Integer minScore, boolean onlyFollowedUsers) {
		AuthDAO authDAO = getAuthDAO(eventId);
		String orderByArray[] = processAuthScoreOrderByFieldName(orderBy);
		
		List<String> usernames = null;
		if(onlyFollowedUsers){
			usernames = usernameService.getFollowedUsersUsername(eventId);
			if(usernames.isEmpty()){
				return Collections.emptyList();
			}
		}

		Pageable pageable = new ImpalaPageRequest(offset + limit, new Sort(direction, orderByArray));
		List<AuthScore> authScores = authDAO.findEventsByGtEventScoreInUsernameList(pageable, minScore, usernames);
		List<ILoginEventScoreInfo> ret = new ArrayList<>();
		if(offset < authScores.size()){
			Map<String, User> userMap = new HashMap<>();
			for(AuthScore authScore: authScores.subList(offset, authScores.size())){
				String username = authScore.getUserName();
				User user = userMap.get(username);
				if(user == null){
					user = usernameService.findByAuthUsername(eventId, username);
					if(user == null){
						logger.warn("username ({}) was not found in the user collection", username);
						continue;
					}
					
					userMap.put(username, user);
				}
				ret.add(createLoginEventScoreInfo(user, authScore));
			}
		}
		return ret;
	}
	
	public AuthDAO getAuthDAO(LogEventsEnum eventId){
		AuthDAO ret = null;
		switch(eventId){
			case login:
				ret = loginDAO;
				break;
			case ssh:
				ret = sshDAO;
				break;
		default:
			break;
		}
		
		return ret;
	}
	
	public EventScoreDAO getEventScoreDAO(LogEventsEnum eventId){
		EventScoreDAO ret = null;
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
	
	private String[] processAuthScoreOrderByFieldName(String orderBy){
		String defaultOrderBy = AuthScore.EVENT_SCORE_FIELD_NAME;
		if(StringUtils.isEmpty(orderBy)){
			orderBy = defaultOrderBy;
		} else{
			switch(orderBy){
			case "username":
				orderBy = AuthScore.USERNAME_FIELD_NAME;
				break;
			case "userNameScore":
				orderBy = AuthScore.USERNAME_SCORE_FIELD_NAME;
				break;
			case "targetIdScore":
				orderBy = AuthScore.TARGET_ID_SCORE_FIELD_NAME;
				break;
			case "destinationHostname":
				orderBy = AuthScore.TARGET_ID_FIELD_NAME;
				break;
			case "sourceIpScore":
				orderBy = AuthScore.SOURCE_IP_SCORE_FIELD_NAME;
				break;
			case "sourceIp":
				orderBy = AuthScore.SOURCE_IP_FIELD_NAME;
				break;
			case "eventTimeScore":
				orderBy = AuthScore.EVENT_TIME_SCORE_FIELD_NAME;
				break;
			case "eventTime":
				orderBy = AuthScore.EVENT_TIME_FIELD_NAME;
				break;
			case "errorCodeScore":
				orderBy = AuthScore.ERROR_CODE_SCORE_FIELD_NAME;
				break;
			case "errorCode":
				orderBy = AuthScore.ERROR_CODE_FIELD_NAME;
				break;
			default:
				orderBy = defaultOrderBy;
				break;
			}
		}
		
		String ret[];
		if(!orderBy.equals(AuthScore.EVENT_TIME_FIELD_NAME)){
			ret = new String[2];
			ret[0] = orderBy;
			ret[1] = AuthScore.EVENT_TIME_FIELD_NAME;
		} else{
			ret = new String[1];
			ret[0] = orderBy;
		}
		
		return ret;
	}
	
	private String[] processVpnScoreOrderByFieldName(String orderBy){
		String defaultOrderBy = vpnDAO.getEventScoreFieldName();
		if(StringUtils.isEmpty(orderBy)){
			orderBy = defaultOrderBy;
		}
		
		String ret[];
		if(!orderBy.equals(vpnDAO.getEventTimeFieldName())){
			ret = new String[2];
			ret[0] = orderBy;
			ret[1] = vpnDAO.getEventTimeFieldName();
		} else{
			ret = new String[1];
			ret[0] = orderBy;
		}
		
		return ret;
	}
	
	private ILoginEventScoreInfo createLoginEventScoreInfo(User user, AuthScore authScore){
		LoginEventScoreInfo ret = new LoginEventScoreInfo(user, authScore);
		
		return ret;
	}
	
	
	
	
	
	
	
	
	
	
	
	
	@Override
	public List<IVpnEventScoreInfo> getUserSuspiciousVpnEvents(String userId, int offset, int limit, String orderBy, Direction direction, int minScore) {
		User user = userRepository.findOne(userId);
		if(user == null){
			throw new UnknownResourceException(String.format("user with id [%s] does not exist", userId));
		}
		ApplicationUserDetails applicationUserDetails = userService.getApplicationUserDetails(user, UserApplication.vpn);
		if(applicationUserDetails == null || applicationUserDetails.getUserName() == null) {
			return Collections.emptyList();
		}
		String orderByArray[] = processVpnScoreOrderByFieldName(orderBy);
		String vpnUserNameString = user.getUsername();//applicationUserDetails.getUserName();
		Pageable pageable = new ImpalaPageRequest(offset + limit, new Sort(direction, orderByArray));
		List<VpnScore> vpnScores = vpnDAO.findEventsByNormalizedUsernameAndGtEventScore(vpnUserNameString, minScore, pageable);
		List<IVpnEventScoreInfo> ret = new ArrayList<>();
		if(offset < vpnScores.size()){
			for(VpnScore vpnScore: vpnScores.subList(offset, vpnScores.size())){
				ret.add(createVpnEventScoreInfo(user, vpnScore));
			}
		}
		return ret;
	}
	
	@Override
	public List<IVpnEventScoreInfo> getSuspiciousVpnEvents(int offset, int limit, String orderBy, Direction direction, Integer minScore, boolean onlyFollowedUsers) {
		String orderByArray[] = processVpnScoreOrderByFieldName(orderBy);
		Pageable pageable = new ImpalaPageRequest(offset + limit, new Sort(direction, orderByArray));
		
		List<String> usernames = null;
		if(onlyFollowedUsers){
			usernames = usernameService.getFollowedUsersUsername(LogEventsEnum.vpn);
			if(usernames.isEmpty()){
				return Collections.emptyList();
			}
		}
		
		List<VpnScore> vpnScores = vpnDAO.findEventsByGtEventScoreInUsernameList(pageable, minScore, usernames);
		List<IVpnEventScoreInfo> ret = new ArrayList<>();
		if(offset < vpnScores.size()){
			Map<String, User> userMap = new HashMap<>();
			for(VpnScore vpnScore: vpnScores.subList(offset, vpnScores.size())){
				String username = vpnScore.getUsername();
				User user = userMap.get(username);
				if(user == null){
					user = usernameService.findByAuthUsername(LogEventsEnum.vpn, username);
					if(user == null){
						logger.warn("vpn username ({}) was not found in the user collection", username);
						continue;
					}
					userMap.put(username, user);
				}

				ret.add(createVpnEventScoreInfo(user, vpnScore));
			}
		}
		return ret;
	}
	
	private IVpnEventScoreInfo createVpnEventScoreInfo(User user, VpnScore vpnScore){
		IVpnEventScoreInfo ret = new VpnEventScoreInfo(user, vpnScore);
		
		return ret;
	}
	
	
	
	
	
	

	private static final String SERVICE_NAME_FIELD = "service_name";
	private static final String ACCOUNT_NAME_FIELD = "account_name";
	private static final String WMIEVENTS_TABLE_NAME = "wmievents4769";

	

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
	
	@Override
	public String getFilterRegex(String collectionName, String fieldName){
		if(rowFieldRegexFilter == null){
			return null;
		}
		Map<String, String> collectionFilters = rowFieldRegexFilter.get(collectionName);
		if(collectionFilters == null){
			return null;
		}
		return collectionFilters.get(fieldName);
	}
	
	@Override
	public void addFilter(String collectionName, String fieldName, String regex){
		if(StringUtils.isEmpty(regex)){
			logger.warn("got an empty regex for collection name ({}) and field name ({}). not executing!!!",collectionName, fieldName);
			return;
		}
		if(rowFieldRegexFilter == null){
			rowFieldRegexFilter = new HashMap<>();
		}
		
		Map<String, String> collectionFilters = rowFieldRegexFilter.get(collectionName);
		if(collectionFilters == null){
			collectionFilters = new HashMap<>();
			rowFieldRegexFilter.put(collectionName, collectionFilters);
		}
		
		collectionFilters.put(fieldName, regex);
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		String loginAccountNameRegex = serversListConfiguration.getLoginAccountNameRegex();
		if(!StringUtils.isEmpty(loginAccountNameRegex)){
			addFilter(WMIEVENTS_TABLE_NAME, ACCOUNT_NAME_FIELD, loginAccountNameRegex);
		}
		String loginServiceNameRegex = serversListConfiguration.getLoginServiceRegex();
		if(!StringUtils.isEmpty(loginServiceNameRegex)){
			addFilter(WMIEVENTS_TABLE_NAME, SERVICE_NAME_FIELD, loginServiceNameRegex);
		}
	}
}
