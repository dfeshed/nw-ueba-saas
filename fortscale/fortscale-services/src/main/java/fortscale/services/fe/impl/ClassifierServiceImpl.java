package fortscale.services.fe.impl;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

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
import org.springframework.jdbc.core.ColumnMapRowMapper;
import org.springframework.jdbc.core.JdbcOperations;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;

import fortscale.domain.core.ApplicationUserDetails;
import fortscale.domain.core.ClassifierScore;
import fortscale.domain.core.User;
import fortscale.domain.core.dao.UserRepository;
import fortscale.domain.events.LogEventsEnum;
import fortscale.domain.fe.AuthScore;
import fortscale.domain.fe.EventResult;
import fortscale.domain.fe.EventScore;
import fortscale.domain.fe.VpnScore;
import fortscale.domain.fe.dao.AdUsersFeaturesExtractionRepository;
import fortscale.domain.fe.dao.AuthDAO;
import fortscale.domain.fe.dao.EventLoginDayCount;
import fortscale.domain.fe.dao.EventResultRepository;
import fortscale.domain.fe.dao.EventScoreDAO;
import fortscale.domain.fe.dao.Threshold;
import fortscale.domain.fe.dao.VpnDAO;
import fortscale.ebs.EBSPigUDF;
import fortscale.ebs.EventBulkScorer;
import fortscale.services.UserApplication;
import fortscale.services.UserService;
import fortscale.services.analyst.ConfigurationService;
import fortscale.services.configuration.ServersListConfiguration;
import fortscale.services.exceptions.UnknownResourceException;
import fortscale.services.fe.Classifier;
import fortscale.services.fe.ClassifierService;
import fortscale.services.fe.EBSResult;
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
import fortscale.utils.scoring.IEBSResult;
import fortscale.utils.scoring.IQueryResultsScorer;
import fortscale.utils.scoring.impl.QueryResultsScorer;

@Service("classifierService")
public class ClassifierServiceImpl implements ClassifierService, InitializingBean{
	private static Logger logger = Logger.getLogger(ClassifierServiceImpl.class);
	
	private static final String EVENT_SCORE = "eventScore";
	
	
	
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
	public int countAuthEvents(LogEventsEnum eventId, String userId, Date timestamp){
		EventScoreDAO eventScoreDAO = getEventScoreDAO(eventId);
		User user = userRepository.findOne(userId);
		if(user == null){
			throw new UnknownResourceException(String.format("user with id [%s] does not exist", userId));
		}
		if(timestamp == null){
			timestamp = eventScoreDAO.getLastRunDate();
		}
		String logUsername = usernameService.getAuthLogUsername(eventId, user);
		if(logUsername != null){
			return eventScoreDAO.countNumOfEventsByNormalizedUsername(timestamp, user.getUsername());
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
	public int countAuthEvents(LogEventsEnum eventId, Date timestamp, String userId, int minScore){
		EventScoreDAO eventScoreDAO = getEventScoreDAO(eventId);
		User user = userRepository.findOne(userId);
		if(user == null){
			throw new UnknownResourceException(String.format("user with id [%s] does not exist", userId));
		}
		if(timestamp == null){
			timestamp = eventScoreDAO.getLastRunDate();
		}
		String logUsername = usernameService.getAuthLogUsername(eventId, user);
		if(logUsername != null){
			return eventScoreDAO.countNumOfEventsByNormalizedUsernameAndGtEScore(timestamp, user.getUsername(), minScore);
		} else{
			return 0;
		}
	}
	
	@Override
	public int countAuthEvents(LogEventsEnum eventId, Date timestamp, int minScore, boolean onlyFollowedUsers){
		EventScoreDAO eventScoreDAO = getEventScoreDAO(eventId);
		if(timestamp == null){
			timestamp = eventScoreDAO.getLastRunDate();
		}

		List<String> usernames = null;
		if(onlyFollowedUsers){
			usernames = usernameService.getFollowedUsersUsername(eventId);
			if(usernames.isEmpty()){
				return 0;
			}
		}
		return eventScoreDAO.countNumOfEventsByGTEScoreAndNormalizedUsernameList(timestamp, minScore, usernames);
	}

	@Override
	public List<ILoginEventScoreInfo> getUserSuspiciousAuthEvents(LogEventsEnum eventId, String userId, Date timestamp, int offset, int limit, String orderBy, Direction direction, int minScore) {
		AuthDAO authDAO = getAuthDAO(eventId);
		if(timestamp == null){
			timestamp = authDAO.getLastRunDate();
		}
		
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
		List<AuthScore> authScores = authDAO.findEventsByNormalizedUsernameAndTimestampGtEventScore(user.getUsername(), timestamp, minScore, pageable);
		List<ILoginEventScoreInfo> ret = new ArrayList<>();
		if(offset < authScores.size()){
			for(AuthScore authScore: authScores.subList(offset, authScores.size())){
				ret.add(createLoginEventScoreInfo(user, authScore));
			}
		}
		return ret;
	}
	
	@Override
	public int countAuthEvents(LogEventsEnum eventId, Date timestamp){
		AuthDAO authDAO = getAuthDAO(eventId);
		if(timestamp == null){
			timestamp = authDAO.getLastRunDate();
		}
		return authDAO.countNumOfEvents(timestamp);
	}

	@Override
	public List<ILoginEventScoreInfo> getSuspiciousAuthEvents(LogEventsEnum eventId, Date timestamp, int offset, int limit, String orderBy, Direction direction, Integer minScore, boolean onlyFollowedUsers) {
		AuthDAO authDAO = getAuthDAO(eventId);
		if(timestamp == null){
			timestamp = authDAO.getLastRunDate();
		}
		String orderByArray[] = processAuthScoreOrderByFieldName(orderBy);
		
		List<String> usernames = null;
		if(onlyFollowedUsers){
			usernames = usernameService.getFollowedUsersUsername(eventId);
			if(usernames.isEmpty()){
				return Collections.emptyList();
			}
		}

		Pageable pageable = new ImpalaPageRequest(offset + limit, new Sort(direction, orderByArray));
		List<AuthScore> authScores = authDAO.findEventsByTimestampGtEventScoreInUsernameList(timestamp, pageable, minScore, usernames);
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
			case "userScore":
				orderBy = AuthScore.GLOBAL_SCORE_FIELD_NAME;
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
	public List<IVpnEventScoreInfo> getUserSuspiciousVpnEvents(String userId, Date timestamp, int offset, int limit, String orderBy, Direction direction, int minScore) {
		if(timestamp == null){
			timestamp = vpnDAO.getLastRunDate();
		}
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
		List<VpnScore> vpnScores = vpnDAO.findEventsByNormalizedUsernameAndTimestampGtEventScore(vpnUserNameString, timestamp, minScore, pageable);
		List<IVpnEventScoreInfo> ret = new ArrayList<>();
		if(offset < vpnScores.size()){
			for(VpnScore vpnScore: vpnScores.subList(offset, vpnScores.size())){
				ret.add(createVpnEventScoreInfo(user, vpnScore));
			}
		}
		return ret;
	}
	
	@Override
	public List<IVpnEventScoreInfo> getSuspiciousVpnEvents(Date timestamp, int offset, int limit, String orderBy, Direction direction, Integer minScore, boolean onlyFollowedUsers) {
		if(timestamp == null){
			timestamp = vpnDAO.getLastRunDate();
		}
		String orderByArray[] = processVpnScoreOrderByFieldName(orderBy);
		Pageable pageable = new ImpalaPageRequest(offset + limit, new Sort(direction, orderByArray));
		
		List<String> usernames = null;
		if(onlyFollowedUsers){
			usernames = usernameService.getFollowedUsersUsername(LogEventsEnum.vpn);
			if(usernames.isEmpty()){
				return Collections.emptyList();
			}
		}
		
		List<VpnScore> vpnScores = vpnDAO.findEventsByTimestampGtEventScoreInUsernameList(timestamp, pageable, minScore, usernames);
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
	
	
	
	
	
	

	private static final String WMIEVENTS_TIME_FIELD = "timegenerated";
	private static final String MACHINE_NAME_FIELD = "machine_name";
	private static final String CLIENT_ADDRESSE_FIELD = "client_address";
	private static final String SERVICE_NAME_FIELD = "service_name";
	private static final String ACCOUNT_NAME_FIELD = "account_name";
	private static final String WMIEVENTS_TABLE_NAME = "wmievents4769";
	private static final String SSH_TABLE_NAME = "sshdata";
	
	public EBSResult getEBSAlgOnAuthQuery(List<Map<String, Object>> resultsMap, String tableName, int offset, int limit, String orderBy, String orderByDirection){
		List<EventBulkScorer.InputStruct> listResults = new ArrayList<EventBulkScorer.InputStruct>((int)resultsMap.size());

		List<String> keys = new ArrayList<>();
		for(String fieldName: resultsMap.get(0).keySet()){
			if(fieldName.equals(MACHINE_NAME_FIELD) || fieldName.equals(CLIENT_ADDRESSE_FIELD)){
				continue;
			}
			keys.add(fieldName);
		}
		
		for (Map<String, Object> map : resultsMap) {
			if(filterRowResults(map, tableName)){
				continue;
			}

			List<String> workingSet = new ArrayList<String>(keys.size() + 1);
			List<String> allData = new ArrayList<String>(keys.size() + 2);
			String machineName = map.get(MACHINE_NAME_FIELD) != null ? map.get(MACHINE_NAME_FIELD).toString().toLowerCase() : "";
			String clientAddress = map.get(CLIENT_ADDRESSE_FIELD) != null ? map.get(CLIENT_ADDRESSE_FIELD).toString() : "";
			if(!StringUtils.isEmpty(machineName)){
				workingSet.add(machineName);
			} else{
				workingSet.add(clientAddress);
			}
			allData.add(machineName);
			allData.add(clientAddress);
			for (int i = 0; i < keys.size(); i++) {
				String keyString = keys.get(i);
				Object tmp = map.get(keyString);
				String val = null;
				if(tmp != null) {
					val = tmp.toString();
				} else {
					logger.warn("no value returned for the column {}", keyString);
					val ="";
				}
				allData.add(val);
				if(keyString.equals(WMIEVENTS_TIME_FIELD)){
					try {
						val = EBSPigUDF.normalized_date_string(val);
					} catch (Exception e) {
						logger.warn("got the following event while trying to normalize date", e);
					}
				}
				workingSet.add(val);
				
			}
			EventBulkScorer.InputStruct inp = new EventBulkScorer.InputStruct();
			inp.working_set = workingSet;
			inp.all_data = allData;
			listResults.add(inp);
		}
				
		keys.add(0, CLIENT_ADDRESSE_FIELD);
		keys.add(0, MACHINE_NAME_FIELD);

		EventBulkScorer ebs = new EventBulkScorer();
		EventBulkScorer.EBSResult ebsresult = ebs.work( listResults );
		
		sortEventScoreList(keys, ebsresult, WMIEVENTS_TIME_FIELD, orderBy, orderByDirection);
		
		List<Map<String, Object>> eventResultList = new ArrayList<>();
		int toIndex = offset + limit;
		if(toIndex > ebsresult.event_score_list.size()) {
			toIndex = ebsresult.event_score_list.size();
		}
		
		for (EventBulkScorer.EventScoreStore eventScore : ebsresult.event_score_list.subList(offset, toIndex)) {
			Map<String, Object> eventMap = new HashMap<>();
			String val = eventScore.event.get(0);
			eventMap.put(keys.get(0), val);
			eventMap.put(keys.get(1), eventScore.event.get(1));
			if(StringUtils.isEmpty(val)){
				eventMap.put(formatKeyScore(keys.get(1)), eventScore.explain.get(1));
			} else{
				eventMap.put(formatKeyScore(keys.get(0)), eventScore.explain.get(0));
			}
			for (int i=2;i<eventScore.event.size();i++) {
				eventMap.put(keys.get(i), eventScore.event.get(i));
				eventMap.put(formatKeyScore(keys.get(i)), eventScore.explain.get(i-1));
			}
			eventMap.put(EVENT_SCORE, (double)Math.round(eventScore.score));
			eventResultList.add(eventMap);
		}
				
		return new EBSResult(eventResultList, ebsresult.global_score, offset, ebsresult.event_score_list.size());
	}
	
	private String formatKeyScore(String key){
		return String.format("%sscore",key);
	}
	
	private boolean filterRowResults(Map<String, Object> rowVals, String tableName){
		if(tableName == null){
			return false;
		}
		boolean isFilter = false;
		Map<String, String> filters = rowFieldRegexFilter.get(tableName);
		if(filters != null){
			
			for(Entry<String, String> entry: filters.entrySet()){
				String val = (String)rowVals.get(entry.getKey());
				if(val != null && val.matches(entry.getValue())){
					logger.debug("filtering the event with {} ({}) by regex ({})", entry.getKey(), val, entry.getValue());
					isFilter = true;
					break;
				}
			}
		}
		
		return isFilter;
	}
	
	private void sortEventScoreList(List<String> fieldNames, EventBulkScorer.EBSResult ebsresult, String timeFieldName, String orderBy, String orderByDirection){
		Comparator<EventBulkScorer.EventScoreStore> comparator = new OrderByEventScoreDesc();
		if(orderBy != null && !EVENT_SCORE.equalsIgnoreCase(orderBy)){
			int i = 0;
			for(; i < fieldNames.size(); i++){
				if(fieldNames.get(i).equalsIgnoreCase(orderBy)){
					break;
				}
			}
			if(i < fieldNames.size()){
				if(timeFieldName != null && timeFieldName.equalsIgnoreCase(orderBy)){
					comparator = new OrderByEventTime(impalaParser, i, orderByDirection);
				} else{
					comparator = new OrderByEventStringField(i, orderByDirection);
				}
			}
		}
		Collections.sort(ebsresult.event_score_list, comparator);
	}
	
	private EBSResult processEbsResults(List<String> keys, List<EventBulkScorer.InputStruct> listResults, int offset, int limit, String timeFieldName, List<String> fieldNamesFilter, String orderBy, String orderByDirection){
		EventBulkScorer ebs = new EventBulkScorer();
		EventBulkScorer.EBSResult ebsresult = ebs.work( listResults );
		
		sortEventScoreList(keys, ebsresult, timeFieldName, orderBy, orderByDirection);
		
		List<Map<String, Object>> eventResultList = new ArrayList<>();
		int toIndex = offset + limit;
		if(toIndex > ebsresult.event_score_list.size()) {
			toIndex = ebsresult.event_score_list.size();
		}
		
		for (EventBulkScorer.EventScoreStore eventScore : ebsresult.event_score_list.subList(offset, toIndex)) {
			Map<String, Object> eventMap = new HashMap<>();
			int i=0;
			for (;i<keys.size();i++) {
				eventMap.put(keys.get(i), eventScore.event.get(i));
				eventMap.put(formatKeyScore(keys.get(i)), eventScore.explain.get(i));
			}
			for(String fieldName: fieldNamesFilter){
				eventMap.put(fieldName, eventScore.event.get(i));
			}
			eventMap.put(EVENT_SCORE, (double)Math.round(eventScore.score));
			eventResultList.add(eventMap);
		}
		
		return new EBSResult(eventResultList, ebsresult.global_score, offset, ebsresult.event_score_list.size());
	}
	
	private static final String VPN_DATA_TABLENAME = "vpndata";
	private static final String VPN_TIME_FIELD = "date_time";
	private static final String SSH_TIME_FIELD = "date_time";
	
	public EBSResult getSimpleEBSAlgOnQuery(List<Map<String, Object>> resultsMap, String tableName, String timeFieldName, List<String> fieldNamesFilter, int offset, int limit, String orderBy, String orderByDirection){
		List<EventBulkScorer.InputStruct> listResults = new ArrayList<EventBulkScorer.InputStruct>((int)resultsMap.size());

		List<String> keys = new ArrayList<>();
		for(String fieldName: resultsMap.get(0).keySet()){
			if(fieldNamesFilter.contains(fieldName)){
				continue;
			}
			keys.add(fieldName);
		}
		for (Map<String, Object> map : resultsMap) {
			if(filterRowResults(map, tableName)){
				continue;
			}

			List<String> workingSet = new ArrayList<String>(keys.size());
			List<String> allData = new ArrayList<String>(keys.size());
			for (int i = 0; i < keys.size(); i++) {
				String fieldName = keys.get(i);
				Object tmp = map.get(fieldName);
				
				processFieldRow(tmp, fieldName, timeFieldName, workingSet, allData);
			}
			
			for(String fieldName: fieldNamesFilter){
				processFieldRow(map.get(fieldName), fieldName, timeFieldName, null, allData);
			}
			
			EventBulkScorer.InputStruct inp = new EventBulkScorer.InputStruct();
			inp.working_set = workingSet;
			inp.all_data = allData;
			listResults.add(inp);
		}

		return processEbsResults(keys, listResults, offset, limit, timeFieldName, fieldNamesFilter, orderBy, orderByDirection);
	}
	
	private void processFieldRow(Object tmp, String fieldName, String timeFieldName, List<String> workingSet, List<String> allData){
		String val = null;
		if(tmp != null) {
			val = tmp.toString();
		} else {
			logger.warn("no value returned for the column {}", fieldName);
			val ="";
		}
		allData.add(val);
		if(workingSet != null){
			if(timeFieldName != null && fieldName.equals(timeFieldName)){
				try {
					val = EBSPigUDF.normalized_date_string(val);
				} catch (Exception e) {
					logger.warn("got the following event while trying to normalize date", e);
				}
			}
			workingSet.add(val);
		}
	}
	
	private static final String VPN_STATUS_GLOBAL_SCORE_VALUE = "SUCCESS";
	
	@Override
	public EBSResult getEBSAlgOnQuery(String sqlQuery, int offset, int limit, String orderBy, String orderByDirection, Integer minScore){
		String timestampFieldName = getTimestampFieldName(sqlQuery);
		EBSResult ebsResult = findEBSAlgOnQuery(sqlQuery, offset, limit, orderBy, orderByDirection, timestampFieldName, minScore);
		if(ebsResult != null){
			return ebsResult;
		}
		
		List<Map<String, Object>> resultsMap = impalaJdbcTemplate.query(sqlQuery, new ColumnMapRowMapper());
		if(resultsMap.size() == 0) {
			return new EBSResult(null, null,0, 0);
		}

		boolean isRunThreadForSaving = true;
		if(minScore != null){
			isRunThreadForSaving = false;
		}
		if(sqlQuery.contains(WMIEVENTS_TABLE_NAME)){
			ebsResult = getEBSAlgOnAuthQuery(resultsMap, WMIEVENTS_TABLE_NAME, 0, resultsMap.size(), orderBy, orderByDirection);
		} else if(sqlQuery.contains(SSH_TABLE_NAME)){
			Set<String> timeFieldNameSet = new HashSet<>();
			timeFieldNameSet.add(timestampFieldName);
			IQueryResultsScorer queryResultsScorer = new QueryResultsScorer();
			IEBSResult tmp = queryResultsScorer.runEBSOnQueryResults(resultsMap, rowFieldRegexFilter.get(SSH_TABLE_NAME), timeFieldNameSet, Collections.<String>emptySet(), null, null);
			isRunThreadForSaving = false;
			ebsResult = new EBSResult(tmp.getResultsList(), tmp.getGlobalScore(), 0, tmp.getResultsList().size());
		} else if(sqlQuery.contains(VPN_DATA_TABLENAME)){
			Set<String> fieldNamesFilterSet = new HashSet<>();
			fieldNamesFilterSet.add(vpnDAO.getLocalIpFieldName());
			IQueryResultsScorer queryResultsScorer = new QueryResultsScorer();
			Set<String> timeFieldNameSet = new HashSet<>();
			timeFieldNameSet.add(timestampFieldName);
			IEBSResult tmp = queryResultsScorer.runEBSOnQueryResults(resultsMap, rowFieldRegexFilter.get(VPN_DATA_TABLENAME), timeFieldNameSet, fieldNamesFilterSet, vpnDAO.getStatusFieldName(), VPN_STATUS_GLOBAL_SCORE_VALUE);
			isRunThreadForSaving = false;
			ebsResult = new EBSResult(tmp.getResultsList(), tmp.getGlobalScore(), 0, tmp.getResultsList().size());
		} else{
			ebsResult = getSimpleEBSAlgOnQuery(resultsMap, null, null,Collections.<String>emptyList(), 0, resultsMap.size(), orderBy, orderByDirection);
		}
		if(!isRunThreadForSaving){
			saveEBSResultsOnAuthQuery(sqlQuery, ebsResult, timestampFieldName, false);
			ebsResult = findEBSAlgOnQuery(sqlQuery, offset, limit, orderBy, orderByDirection, timestampFieldName, minScore);
		} else{
			saveEBSResultsOnAuthQuery(sqlQuery, ebsResult, timestampFieldName, true);
			
			int toIndex = offset + limit;
			if(toIndex > ebsResult.getResultsList().size()) {
				toIndex = ebsResult.getResultsList().size();
			}
			ebsResult = new EBSResult(ebsResult.getResultsList().subList(offset, toIndex), ebsResult.getGlobalScore(), offset, ebsResult.getTotal());
		}
		return ebsResult;
	}
	
	private String getTimestampFieldName(String sqlQuery){
		String timestampFieldName = null;
		if(sqlQuery.contains(WMIEVENTS_TABLE_NAME)){
			timestampFieldName = WMIEVENTS_TIME_FIELD;
		} else if(sqlQuery.contains(VPN_DATA_TABLENAME)){
			timestampFieldName = VPN_TIME_FIELD;
		} else if(sqlQuery.contains(SSH_TABLE_NAME)){
			timestampFieldName = SSH_TIME_FIELD;
		}
		return timestampFieldName;
	}
	
	private void saveEBSResultsOnAuthQuery(final String sqlQuery, final EBSResult ebsResult, final String timestampFieldName, boolean isRunThread){
		if(isRunThread){
			Runnable task = new Runnable() {
				@Override
				public void run(){
					EBSResultsOnAuthQuerySaver authQuerySaver = new EBSResultsOnAuthQuerySaver();
					authQuerySaver.save(sqlQuery, ebsResult, timestampFieldName);
				}
			};
			mongoDbWriterExecuter.submit(task);
		} else{
			EBSResultsOnAuthQuerySaver authQuerySaver = new EBSResultsOnAuthQuerySaver();
			authQuerySaver.save(sqlQuery, ebsResult, timestampFieldName);
		}
	}
	
	class EBSResultsOnAuthQuerySaver{
		public void save(final String sqlQuery, final EBSResult ebsResult, final String timestampFieldName){
			List<EventResult> eventResults = new ArrayList<>();
			DateTime date = new DateTime();
			for(Map<String, Object> result: ebsResult.getResultsList()){
				EventResult eventResult = new EventResult();
				eventResult.setAttributes(result);
				eventResult.setGlobalScore(ebsResult.getGlobalScore());
				eventResult.setSqlQuery(sqlQuery);
				eventResult.setLastRetrieved(date);
				eventResult.setCreatedAt(date);
				eventResult.setTotal(ebsResult.getTotal());
				
				Double eventScore = (Double) result.get(EVENT_SCORE);
				eventResult.setEventScore(eventScore);
				
				if(timestampFieldName != null){
					String dateString = (String) result.get(timestampFieldName);
					if(dateString != null){
						try {
							DateTime eventTime = new DateTime(impalaParser.parseTimeDate(dateString));
							eventResult.setEventTime(eventTime);
						} catch (ParseException e) {
							logger.warn("recieve date ({}) in the wrong format for the query ({})", dateString, sqlQuery);
						}
					}
				}
				
				eventResults.add(eventResult);
			}
			eventResultRepository.save(eventResults);
		}
	}
	
	private EBSResult findEBSAlgOnQuery(String query, int offset, int limit, String orderBy, String orderByDirection, String timestampFieldName, Integer minScore){
		Direction direction = Direction.DESC;
		if(!"desc".equalsIgnoreCase(orderByDirection)){
			direction = Direction.ASC;
		}
		String fieldName = EventResult.eventScoreField;
		if(!StringUtils.isEmpty(orderBy) && !orderBy.equalsIgnoreCase(EVENT_SCORE)){
			if(orderBy.equalsIgnoreCase(timestampFieldName)){
				fieldName = EventResult.eventTimeField;
			}else{
				fieldName = EventResult.getAttributesAttributeNameField(orderBy);
			}
		}
		
		int pageSize = limit;
		if(offset % limit != 0){
			pageSize = offset + limit;
		}

		int page = offset/pageSize;
		Pageable pageable = new PageRequest(page, pageSize, direction, fieldName);
		DateTime createdAt = eventResultRepository.getLatestCreatedAt();
		List<EventResult> eventResults = eventResultRepository.findEventResultsBySqlQueryAndCreatedAtAndGtMinScore(query, createdAt, minScore, pageable);
		if(eventResults == null || eventResults.size() == 0){
			return null;
		}
		int total = eventResults.get(0).getTotal();
		double globalScore = eventResults.get(0).getGlobalScore();
		List<Map<String, Object>> resultsList = new ArrayList<>();
		int fromIndex = offset % pageSize;
		int toIndex = fromIndex + limit;
		if(toIndex > eventResults.size()){
			toIndex = eventResults.size();
		}
		for(EventResult eventResult: eventResults.subList(fromIndex, toIndex)){
			resultsList.add(eventResult.getAttributes());
		}
		
		return new EBSResult(resultsList, globalScore, offset, total);
	}
	
	public static class OrderByEventScoreDesc implements Comparator<EventBulkScorer.EventScoreStore>{

		@Override
		public int compare(EventBulkScorer.EventScoreStore o1, EventBulkScorer.EventScoreStore o2) {
			return o2.score > o1.score ? 1 : (o2.score < o1.score ? -1 : 0);
		}
		
	}
	
	public static class OrderByEventTime implements Comparator<EventBulkScorer.EventScoreStore>{
		private int fieldIndex;
		private ImpalaParser impalaParser;
		private int isDesc;
		
		public OrderByEventTime(ImpalaParser impalaParser, int fieldIndex, String orderByDirection){
			this.fieldIndex = fieldIndex;
			this.impalaParser = impalaParser;
			if("desc".equalsIgnoreCase(orderByDirection)){
				this.isDesc = 1;
			} else{
				this.isDesc = -1;
			}
		}

		@Override
		public int compare(EventBulkScorer.EventScoreStore o1, EventBulkScorer.EventScoreStore o2) {
			Long time1 = null;
			Long time2 = null;
			try {
				time1 = impalaParser.parseTimeDate(o1.event.get(fieldIndex)).getTime();
				time2 = impalaParser.parseTimeDate(o2.event.get(fieldIndex)).getTime();
			} catch (ParseException e) {
				logger.error("wrong time format ({}, {})", o1.event.get(fieldIndex), o2.event.get(fieldIndex));
				return 0;
			}
			
			int ret = time2 > time1 ? 1 : (time2 < time1 ? -1 : 0);
			return ret*isDesc;
		}
		
	}
	
	public static class OrderByEventStringField implements Comparator<EventBulkScorer.EventScoreStore>{
		private int fieldIndex;
		private int isDesc;
		
		public OrderByEventStringField(int fieldIndex, String orderByDirection){
			this.fieldIndex = fieldIndex;
			if("desc".equalsIgnoreCase(orderByDirection)){
				this.isDesc = 1;
			} else{
				this.isDesc = -1;
			}
		}

		@Override
		public int compare(EventBulkScorer.EventScoreStore o1, EventBulkScorer.EventScoreStore o2) {
			String val1 = o1.event.get(fieldIndex);
			String val2 = o2.event.get(fieldIndex);
			
			int ret = val2.compareToIgnoreCase(val1);
			return ret*isDesc;
		}		
	}
	
	@Override
	public Long getLatestRuntime(String tableName) {
		Long retLong = null;
		if(loginDAO.getTableName().equals(tableName)) {
			retLong = loginDAO.getLastRuntime();
		} else if(sshDAO.getTableName().equals(tableName)) {
			retLong = sshDAO.getLastRuntime();
		} else if (vpnDAO.getTableName().equals(tableName)) {
			retLong = vpnDAO.getLastRuntime();
		}
		return retLong;
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
