package fortscale.services.fe.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.jdbc.core.ColumnMapRowMapper;
import org.springframework.jdbc.core.JdbcOperations;
import org.springframework.stereotype.Service;

import fortscale.domain.ad.dao.UserMachineDAO;
import fortscale.domain.core.ApplicationUserDetails;
import fortscale.domain.core.ClassifierScore;
import fortscale.domain.core.User;
import fortscale.domain.core.dao.UserRepository;
import fortscale.domain.fe.AuthScore;
import fortscale.domain.fe.VpnScore;
import fortscale.domain.fe.dao.AdUsersFeaturesExtractionRepository;
import fortscale.domain.fe.dao.AuthDAO;
import fortscale.domain.fe.dao.Threshold;
import fortscale.domain.fe.dao.VpnDAO;
import fortscale.ebs.EBSPigUDF;
import fortscale.ebs.EventBulkScorer;
import fortscale.services.UserApplication;
import fortscale.services.UserService;
import fortscale.services.analyst.ConfigurationService;
import fortscale.services.exceptions.InvalidValueException;
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
import fortscale.utils.impala.ImpalaPageRequest;
import fortscale.utils.logging.Logger;

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
	private AuthDAO authDAO;
	
	@Autowired
	private VpnDAO vpnDAO;
	
	@Autowired
	private UserMachineDAO userMachineDAO;
	
	@Autowired
	private UserService userService;
	
	@Autowired
	private ConfigurationService configurationService;
	
	@Value("${login.service.name.regex:}")
	private String loginServiceNameRegex;
	
	@Value("${login.account.name.regex:}")
	private String loginAccountNameRegex;
	
	private Map<String, Map<String, String>> rowFieldRegexFilter;
	
	
	
	

	
	public void setLoginServiceNameRegex(String loginServiceNameRegex) {
		this.loginServiceNameRegex = loginServiceNameRegex;
	}

	public void setLoginAccountNameRegex(String loginAccountNameRegex) {
		this.loginAccountNameRegex = loginAccountNameRegex;
	}

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
			int percent = (int)((threshold.getCount()/(double)total)*100);
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
	public List<ISuspiciousUserInfo> getSuspiciousUsersByScore(String classifierId, String severityId, int page, int size) {
		return getTopUsers(classifierId, severityId, new ThresholdNoFilter(),page,size, User.getClassifierScoreCurrentScoreField(classifierId), User.getClassifierScoreCurrentTrendScoreField(classifierId));
	}
	
	@Override
	public List<ISuspiciousUserInfo> getSuspiciousUsersByTrend(String classifierId, String severityId, int page, int size) {
		return getTopUsers(classifierId, severityId, new ThresholdTrendFilter(),page,size, User.getClassifierScoreCurrentTrendScoreField(classifierId), User.getClassifierScoreCurrentScoreField(classifierId));
	}

	private List<ISuspiciousUserInfo> getTopUsers(String classifierId, String severityId, ThresholdFilter thresholdFilter, int page, int size, String... sortingFieldsName) {
		Classifier.validateClassifierId(classifierId);
		
		Range severityRange = getRange(severityId);
		
		Pageable pageable = new PageRequest(page, size, Direction.DESC, sortingFieldsName);
		List<User> users = userRepository.findByClassifierIdAndScoreBetween(classifierId, severityRange.getLowestVal(), severityRange.getUpperVal(), pageable);
		List<ISuspiciousUserInfo> ret = new ArrayList<>();
		for(User user: users){
			ISuspiciousUserInfo suspiciousUserInfo = createSuspiciousUserInfo(classifierId, user);
			if(!thresholdFilter.hasPassed(suspiciousUserInfo)){
				break;
			}
			ret.add(suspiciousUserInfo);
		}
		return ret;
		
//		List<ISuspiciousUserInfo> ret = Collections.emptyList();
//		if(classifierId.equals(Classifier.ad.getId())){
//			ret = getAdSuspiciousUsers(classifierId, severityId);
//		} else if(classifierId.equals(Classifier.auth.getId())){
//			ret = getAuthSuspiciousUsers(classifierId, severityId);
//		} else if(classifierId.equals(Classifier.vpn.getId())){
//			ret = getVpnSuspiciousUsers(classifierId, severityId);
//		} else {
//			throw new InvalidValueException(String.format("no such classifier id [%s]", classifierId));
//		}
//		
//		return ret;
	}
	
	
	private List<ISuspiciousUserInfo> getVpnSuspiciousUsers(String classifierId, String severityId) {
		Date lastRun = vpnDAO.getLastRunDate();
		Range severityRange = getRange(severityId);
		List<VpnScore> vpnScores = vpnDAO.findByTimestampAndGlobalScoreBetweenSortByEventScore(lastRun, severityRange.getLowestVal(), severityRange.getUpperVal(), 10);
		List<ISuspiciousUserInfo> ret = new ArrayList<>();
		for(VpnScore vpnScore: vpnScores){
			User user = userRepository.findByUsername(vpnScore.getUserName().toLowerCase());
			if(user == null){
				logger.error("user with vpn username ({}) was not found", vpnScore.getUserName());
				continue;
			}
			ret.add(createSuspiciousUserInfo(classifierId, user));
		}
		return ret;
	}
	
	private List<ISuspiciousUserInfo> getAuthSuspiciousUsers(String classifierId, String severityId) {
		Date lastRun = authDAO.getLastRunDate();
		Range severityRange = getRange(severityId);
		List<AuthScore> authScores = authDAO.findByTimestampAndGlobalScoreBetweenSortByEventScore(lastRun, severityRange.getLowestVal(), severityRange.getUpperVal(), 10);
		List<ISuspiciousUserInfo> ret = new ArrayList<>();
		for(AuthScore authScore: authScores){
			User user = userRepository.findByUsername(authScore.getUserName().toLowerCase());
			if(user == null){
				logger.error("user with username ({}) was not found", authScore.getUserName());
				continue;
			}
			ret.add(createSuspiciousUserInfo(classifierId, user));
		}
		return ret;
	}
	
	private List<ISuspiciousUserInfo> getAdSuspiciousUsers(String classifierId, String severityId) {
		Range severityRange = getRange(severityId);
		
		Pageable pageable = new PageRequest(0, 10, Direction.DESC, User.getClassifierScoreCurrentScoreField(classifierId));
		List<User> users = userRepository.findByClassifierIdAndScoreBetween(classifierId, severityRange.getLowestVal(), severityRange.getUpperVal(), pageable);
		List<ISuspiciousUserInfo> ret = new ArrayList<>();
		for(User user: users){
			ret.add(createSuspiciousUserInfo(classifierId, user));
		}
		return ret;
	}
	
	private SuspiciousUserInfo createSuspiciousUserInfo(String classifierId, User user){
		ClassifierScore classifierScore = user.getScore(classifierId);
		int trendSign = classifierScore.getTrend() > 0 ? 1 : -1;
		return new SuspiciousUserInfo(user.getId(), user.getUsername(), (int) Math.round(user.getScore(classifierId).getScore()), Math.round(classifierScore.getTrendScore()*trendSign));
	}
	
	private Range getRange(String severityId){
		if(severityId == null){
			return new Range(0, 101);
		}
		int i = 0;
		for(SeverityElement element: configurationService.getSeverityElements()){
			if(element.getName().equals(severityId)){
				break;
			}
			i++;
		}
		if(configurationService.getSeverityElements().size() == i){
			throw new InvalidValueException(String.format("no such severity id: %s", severityId));
		}
		int lowestVal = configurationService.getSeverityElements().get(i).getValue();
		int upperVal = 100;
		if(i > 0){
			upperVal = configurationService.getSeverityElements().get(i-1).getValue();
		}
		
		return new Range(lowestVal, upperVal);
	}

	class Range{
		private int lowestVal;
		private int upperVal;
		
		public Range(int lowestVal, int upperVal){
			this.lowestVal = lowestVal;
			this.upperVal = upperVal;
		}

		public int getLowestVal() {
			return lowestVal;
		}

		public int getUpperVal() {
			return upperVal;
		}
		
	}
	
	@Override
	public int countLoginEvents(String userId, Date timestamp){
		User user = userRepository.findOne(userId);
		if(user == null){
			throw new UnknownResourceException(String.format("user with id [%s] does not exist", userId));
		}
		if(timestamp == null){
			timestamp = authDAO.getLastRunDate();
		}
		return authDAO.countNumOfEventsByUser(timestamp, user.getUsername());
	}

	@Override
	public List<ILoginEventScoreInfo> getUserSuspiciousLoginEvents(String userId, Date timestamp, int offset, int limit) {
		if(timestamp == null){
			timestamp = authDAO.getLastRunDate();
		}
		User user = userRepository.findOne(userId);
		if(user == null){
			throw new UnknownResourceException(String.format("user with id [%s] does not exist", userId));
		}
		Pageable pageable = new ImpalaPageRequest(offset + limit, new Sort(Direction.DESC, AuthScore.EVENT_SCORE_FIELD_NAME));
		List<AuthScore> authScores = authDAO.findEventsByUsernameAndTimestamp(user.getUsername(), timestamp, pageable);
		List<ILoginEventScoreInfo> ret = new ArrayList<>();
		if(offset < authScores.size()){
			for(AuthScore authScore: authScores.subList(offset, authScores.size())){
				ret.add(createLoginEventScoreInfo(user, authScore));
			}
		}
		return ret;
	}
	
	@Override
	public int countLoginEvents(Date timestamp){
		if(timestamp == null){
			timestamp = authDAO.getLastRunDate();
		}
		return authDAO.countNumOfEvents(timestamp);
	}

	@Override
	public List<ILoginEventScoreInfo> getSuspiciousLoginEvents(Date timestamp, int offset, int limit) {
		if(timestamp == null){
			timestamp = authDAO.getLastRunDate();
		}
		Pageable pageable = new ImpalaPageRequest(offset + limit, new Sort(Direction.DESC, AuthScore.EVENT_SCORE_FIELD_NAME));
		List<AuthScore> authScores = authDAO.findEventsByTimestamp(timestamp, pageable);
		List<ILoginEventScoreInfo> ret = new ArrayList<>();
		if(offset < authScores.size()){
			Map<String, User> userMap = new HashMap<>();
			for(AuthScore authScore: authScores.subList(offset, authScores.size())){
				String username = authScore.getUserName().toLowerCase();
				User user = userMap.get(username);
				if(user == null){
					user = userRepository.findByUsername(username);
					if(user == null){
						logger.warn("username ({}) was not found in the user collection", username);
						continue;
					} else{
						userMap.put(username, user);
					}
				}
				ret.add(createLoginEventScoreInfo(user, authScore));
			}
		}
		return ret;
	}
	
	private ILoginEventScoreInfo createLoginEventScoreInfo(User user, AuthScore authScore){
		LoginEventScoreInfo ret = new LoginEventScoreInfo(user, authScore);
		
		return ret;
	}
	
	
	
	
	
	
	
	
	
	
	
	@Override
	public int countVpnEvents(String userId, Date timestamp){
		User user = userRepository.findOne(userId);
		if(user == null){
			throw new UnknownResourceException(String.format("user with id [%s] does not exist", userId));
		}
		ApplicationUserDetails applicationUserDetails = userService.getApplicationUserDetails(user, UserApplication.vpn);
		if(applicationUserDetails == null || applicationUserDetails.getUserName() == null) {
			return 0;
		}
		String vpnUserNameString = applicationUserDetails.getUserName();
		if(timestamp == null){
			timestamp = vpnDAO.getLastRunDate();
		}
		return vpnDAO.countNumOfEventsByUser(timestamp, vpnUserNameString);
	}
	
	@Override
	public List<IVpnEventScoreInfo> getUserSuspiciousVpnEvents(String userId, Date timestamp, int offset, int limit) {
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
		String vpnUserNameString = applicationUserDetails.getUserName();
		Pageable pageable = new ImpalaPageRequest(offset + limit, new Sort(Direction.DESC, VpnScore.EVENT_SCORE_FIELD_NAME));
		List<VpnScore> vpnScores = vpnDAO.findEventsByUsernameAndTimestamp(vpnUserNameString, timestamp, pageable);
		List<IVpnEventScoreInfo> ret = new ArrayList<>();
		if(offset < vpnScores.size()){
			for(VpnScore vpnScore: vpnScores.subList(offset, vpnScores.size())){
				ret.add(createVpnEventScoreInfo(user, vpnScore));
			}
		}
		return ret;
	}
	
	@Override
	public int countVpnEvents(Date timestamp){
		if(timestamp == null){
			timestamp = vpnDAO.getLastRunDate();
		}
		return vpnDAO.countNumOfEvents(timestamp);
	}

	@Override
	public List<IVpnEventScoreInfo> getSuspiciousVpnEvents(Date timestamp, int offset, int limit) {
		if(timestamp == null){
			timestamp = vpnDAO.getLastRunDate();
		}
		Pageable pageable = new ImpalaPageRequest(offset + limit, new Sort(Direction.DESC, VpnScore.EVENT_SCORE_FIELD_NAME));
		List<VpnScore> vpnScores = vpnDAO.findEventsByTimestamp(timestamp, pageable);
		List<IVpnEventScoreInfo> ret = new ArrayList<>();
		if(offset < vpnScores.size()){
			Map<String, User> userMap = new HashMap<>();
			for(VpnScore vpnScore: vpnScores.subList(offset, vpnScores.size())){
				String username = vpnScore.getUserName().toLowerCase();
				User user = userMap.get(username);
				if(user == null){
					user = userRepository.findByApplicationUserName(userService.createApplicationUserDetails(UserApplication.vpn, username));
					if(user == null){
						logger.warn("vpn username ({}) was not found in the user collection", username);
						continue;
					} else{
						userMap.put(username, user);
					}
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
	
	@Override
	public EBSResult getEBSAlgOnAuthQuery(List<Map<String, Object>> resultsMap, int offset, int limit){
		List<EventBulkScorer.InputStruct> listResults = new ArrayList<EventBulkScorer.InputStruct>((int)resultsMap.size());

		Set<String> keySet = resultsMap.get(0).keySet();
		keySet.remove(MACHINE_NAME_FIELD);
		keySet.remove(CLIENT_ADDRESSE_FIELD);
		List<String> keys = new ArrayList<>(keySet);
		for (Map<String, Object> map : resultsMap) {
			if(filterRowResults(map, WMIEVENTS_TABLE_NAME)){
				continue;
			}

			List<String> workingSet = new ArrayList<String>(keys.size() + 1);
			List<String> allData = new ArrayList<String>(keys.size() + 2);
			String machineName = map.get(MACHINE_NAME_FIELD) != null ? map.get(MACHINE_NAME_FIELD).toString() : "";
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
		
		Collections.sort(ebsresult.event_score_list, new OrderByEventScoreDesc());
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
	
	private EBSResult processEbsResults(List<String> keys, List<EventBulkScorer.InputStruct> listResults, int offset, int limit){
		EventBulkScorer ebs = new EventBulkScorer();
		EventBulkScorer.EBSResult ebsresult = ebs.work( listResults );
		
		Collections.sort(ebsresult.event_score_list, new OrderByEventScoreDesc());
		List<Map<String, Object>> eventResultList = new ArrayList<>();
		int toIndex = offset + limit;
		if(toIndex > ebsresult.event_score_list.size()) {
			toIndex = ebsresult.event_score_list.size();
		}
		
		for (EventBulkScorer.EventScoreStore eventScore : ebsresult.event_score_list.subList(offset, toIndex)) {
			Map<String, Object> eventMap = new HashMap<>();
			for (int i=0;i<eventScore.event.size();i++) {
				eventMap.put(keys.get(i), eventScore.event.get(i));
				eventMap.put(formatKeyScore(keys.get(i)), eventScore.explain.get(i));
			}
			eventMap.put(EVENT_SCORE, (double)Math.round(eventScore.score));
			eventResultList.add(eventMap);
		}
		
		return new EBSResult(eventResultList, ebsresult.global_score, offset, ebsresult.event_score_list.size());
	}
	
	private static final String VPN_DATA_TABLENAME = "vpndata";
	private static final String VPN_TIME_FIELD = "date_time";
	
	@Override
	public EBSResult getSimpleEBSAlgOnQuery(List<Map<String, Object>> resultsMap, String tableName, String timeFieldName, int offset, int limit){
		List<EventBulkScorer.InputStruct> listResults = new ArrayList<EventBulkScorer.InputStruct>((int)resultsMap.size());

		Set<String> keySet = resultsMap.get(0).keySet();
		List<String> keys = new ArrayList<>(keySet);
		for (Map<String, Object> map : resultsMap) {
			if(filterRowResults(map, tableName)){
				continue;
			}

			List<String> workingSet = new ArrayList<String>(keys.size());
			List<String> allData = new ArrayList<String>(keys.size());
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
				if(timeFieldName != null && keyString.equals(timeFieldName)){
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

		return processEbsResults(keys, listResults, offset, limit);
	}
	
	@Override
	public EBSResult getEBSAlgOnQuery(String query, int offset, int limit){
		List<Map<String, Object>> resultsMap = impalaJdbcTemplate.query(query, new ColumnMapRowMapper());
		if(resultsMap.size() == 0) {
			return new EBSResult(null, null,0, 0);
		}
		
		if(query.contains(WMIEVENTS_TABLE_NAME)){
			return getEBSAlgOnAuthQuery(resultsMap, offset, limit);
		} else if(query.contains(VPN_DATA_TABLENAME)){
			return getSimpleEBSAlgOnQuery(resultsMap, VPN_DATA_TABLENAME, VPN_TIME_FIELD, offset, limit);
		} else{
			return getSimpleEBSAlgOnQuery(resultsMap, null, null, offset, limit);
		}
	}
	
	public static class OrderByEventScoreDesc implements Comparator<EventBulkScorer.EventScoreStore>{

		@Override
		public int compare(EventBulkScorer.EventScoreStore o1, EventBulkScorer.EventScoreStore o2) {
			return o2.score > o1.score ? 1 : (o2.score < o1.score ? -1 : 0);
		}
		
	}
	
	@Override
	public Long getLatestRuntime(String tableName) {
		Long retLong = null;
		if(AuthScore.TABLE_NAME.equals(tableName)) {
			retLong = authDAO.getLastRuntime();
		} else if (VpnScore.TABLE_NAME.equals(tableName)) {
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
		if(!StringUtils.isEmpty(loginAccountNameRegex)){
			addFilter(WMIEVENTS_TABLE_NAME, ACCOUNT_NAME_FIELD, loginAccountNameRegex);
		}
		if(!StringUtils.isEmpty(loginServiceNameRegex)){
			addFilter(WMIEVENTS_TABLE_NAME, SERVICE_NAME_FIELD, loginServiceNameRegex);
		}
	}
}
