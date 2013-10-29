package fortscale.services.fe.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
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
import fortscale.ebs.EventBulkScorer;
import fortscale.services.UserApplication;
import fortscale.services.UserService;
import fortscale.services.exceptions.InvalidValueException;
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

@Service("classifierService")
public class ClassifierServiceImpl implements ClassifierService {
	
	private static final String EVENT_SCORE = "eventScore";
	private static List<SeverityElement> severityOrderedList = getSeverityList();
//	private static Map<String,SeverityElement> severityMap = null;
	
	private Map<String, Classifier> classifiersMap = getClassifiersMap();
	
	private static Map<String, Classifier> getClassifiersMap(){
		Map<String, Classifier> ret = new HashMap<String, Classifier>();
		for(Classifier classifier: Classifier.values()){
			ret.put(classifier.getId(), classifier);
		}
		return ret;
	}
		
	private static List<SeverityElement> getSeverityList(){
		List<SeverityElement> ret = new ArrayList<>();
		ret.add(new SeverityElement("Critical", 90));
		ret.add(new SeverityElement("High", 50));
		ret.add(new SeverityElement("Medium", 10));
		ret.add(new SeverityElement("Low", 0));
		return ret;
	}
	
//	private static Map<String,SeverityElement> getSeverityMap(){
//		if(severityMap == null){
//			Map<String,SeverityElement> tmp = new HashMap<>();
//			for(SeverityElement element: severityOrderedList){
//				tmp.put(element.getName(), element);
//			}
//			severityMap = tmp;
//		}
//		
//		return severityMap;
//	}
	
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

	
	public Classifier getClassifier(String classifierId){
		return classifiersMap.get(classifierId);
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
		List<Threshold> thresholds = new ArrayList<>();
		thresholds.add(new Threshold("All", 0));
		for(SeverityElement element: severityOrderedList){
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
	public List<ISuspiciousUserInfo> getSuspiciousUsers(String classifierId, String severityId) {
		List<ISuspiciousUserInfo> ret = Collections.emptyList();
		if(classifierId.equals(Classifier.ad.getId())){
			ret = getAdSuspiciousUsers(classifierId, severityId);
		} else if(classifierId.equals(Classifier.auth.getId())){
			ret = getAuthSuspiciousUsers(classifierId, severityId);
		} else if(classifierId.equals(Classifier.vpn.getId())){
			ret = getVpnSuspiciousUsers(classifierId, severityId);
		} else {
			throw new InvalidValueException(String.format("no such classifier id [%s]", classifierId));
		}
		
		return ret;
	}
	
	private List<ISuspiciousUserInfo> getVpnSuspiciousUsers(String classifierId, String severityId) {
		Date lastRun = vpnDAO.getLastRunDate();
		Range severityRange = getRange(severityId);
		List<VpnScore> vpnScores = vpnDAO.findByTimestampAndGlobalScoreBetweenSortByEventScore(lastRun, severityRange.getLowestVal(), severityRange.getUpperVal(), 10);
		List<ISuspiciousUserInfo> ret = new ArrayList<>();
		for(VpnScore vpnScore: vpnScores){
			User user = userRepository.findByAdUserPrincipalName(vpnScore.getUserName().toLowerCase());
			if(user == null){
				//TODO: error message.
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
			User user = userRepository.findByAdUserPrincipalName(authScore.getUserName().toLowerCase());
			if(user == null){
				//TODO: error message.
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
		double trend = 0;
		if(!classifierScore.getPrevScores().isEmpty()){
			double prevScore = classifierScore.getPrevScores().get(0).getScore() + 0.00001;
			double curScore = classifierScore.getScore() + 0.00001;
			trend = (int)(((curScore - prevScore) / prevScore) * 10000);
			trend = trend/100;
		}
		return new SuspiciousUserInfo(user.getId(), user.getAdUserPrincipalName(), (int) Math.round(user.getScore(classifierId).getScore()), trend);
	}
	
	private Range getRange(String severityId){
		int i = 0;
		for(SeverityElement element: severityOrderedList){
			if(element.getName().equals(severityId)){
				break;
			}
			i++;
		}
		if(severityOrderedList.size() == i){
			throw new InvalidValueException(String.format("no such severity id: %s", severityId));
		}
		int lowestVal = severityOrderedList.get(i).getValue();
		int upperVal = 100;
		if(i > 0){
			upperVal = severityOrderedList.get(i-1).getValue();
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
	public List<ILoginEventScoreInfo> getUserSuspiciousLoginEvents(String userId, Date timestamp, int offset, int limit) {
		if(timestamp == null){
			timestamp = authDAO.getLastRunDate();
		}
		User user = userRepository.findOne(userId);
		if(user == null){
			return Collections.emptyList();
		}
		Pageable pageable = new ImpalaPageRequest(offset + limit, new Sort(Direction.DESC, AuthScore.EVENT_SCORE_FIELD_NAME));
		List<AuthScore> authScores = authDAO.findEventsByUsernameAndTimestamp(user.getAdUserPrincipalName(), timestamp, pageable);
		List<ILoginEventScoreInfo> ret = new ArrayList<>();
		for(AuthScore authScore: authScores){
			ret.add(createLoginEventScoreInfo(user, authScore));
		}
		return ret;
	}

	@Override
	public List<ILoginEventScoreInfo> getSuspiciousLoginEvents(Date timestamp, int offset, int limit) {
		if(timestamp == null){
			timestamp = authDAO.getLastRunDate();
		}
		Pageable pageable = new ImpalaPageRequest(offset + limit, new Sort(Direction.DESC, AuthScore.EVENT_SCORE_FIELD_NAME));
		List<AuthScore> authScores = authDAO.findEventsByTimestamp(timestamp, pageable);
		List<ILoginEventScoreInfo> ret = new ArrayList<>();
		Map<String, User> userMap = new HashMap<>();
		int skipped = 0;
		for(AuthScore authScore: authScores){
			String username = authScore.getUserName().toLowerCase();
			User user = userMap.get(username);
			if(user == null){
				user = userRepository.findByAdUserPrincipalName(username);
				if(user == null){
					//TODO: warn message
					continue;
				} else{
					userMap.put(username, user);
				}
			}
			if(skipped >= offset){
				ret.add(createLoginEventScoreInfo(user, authScore));
			} else {
				skipped++;
			}
		}
		return ret;
	}
	
	private ILoginEventScoreInfo createLoginEventScoreInfo(User user, AuthScore authScore){
		LoginEventScoreInfo ret = new LoginEventScoreInfo(user, authScore);
		
		return ret;
	}
	
	
	
	
	
	
	
	
	
	
	
	
	@Override
	public List<IVpnEventScoreInfo> getUserSuspiciousVpnEvents(String userId, Date timestamp, int offset, int limit) {
		if(timestamp == null){
			timestamp = vpnDAO.getLastRunDate();
		}
		User user = userRepository.findOne(userId);
		if(user == null){
			return Collections.emptyList();
		}
		ApplicationUserDetails applicationUserDetails = userService.getApplicationUserDetails(user, UserApplication.vpn);
		if(applicationUserDetails == null || applicationUserDetails.getUserName() == null) {
			return Collections.emptyList();
		}
		String vpnUserNameString = applicationUserDetails.getUserName();
		Pageable pageable = new ImpalaPageRequest(offset + limit, new Sort(Direction.DESC, VpnScore.EVENT_SCORE_FIELD_NAME));
		List<VpnScore> vpnScores = vpnDAO.findEventsByUsernameAndTimestamp(vpnUserNameString, timestamp, pageable);
		List<IVpnEventScoreInfo> ret = new ArrayList<>();
		for(VpnScore vpnScore: vpnScores){
			ret.add(createVpnEventScoreInfo(user, vpnScore));
		}
		return ret;
	}

	@Override
	public List<IVpnEventScoreInfo> getSuspiciousVpnEvents(Date timestamp, int offset, int limit) {
		if(timestamp == null){
			timestamp = vpnDAO.getLastRunDate();
		}
		Pageable pageable = new ImpalaPageRequest(offset + limit, new Sort(Direction.DESC, VpnScore.EVENT_SCORE_FIELD_NAME));
		List<VpnScore> vpnScores = vpnDAO.findEventsByTimestamp(timestamp, pageable);
		List<IVpnEventScoreInfo> ret = new ArrayList<>();
		Map<String, User> userMap = new HashMap<>();
		int skipped = 0;
		for(VpnScore vpnScore: vpnScores){
			String username = vpnScore.getUserName().toLowerCase();
			User user = userMap.get(username);
			if(user == null){
				user = userRepository.findByApplicationUserName(userService.createApplicationUserDetails(UserApplication.vpn, username));
				if(user == null){
					//TODO: warn message
					continue;
				} else{
					userMap.put(username, user);
				}
			}
			if(skipped >= offset){
				ret.add(createVpnEventScoreInfo(user, vpnScore));
			} else {
				skipped++;
			}
		}
		return ret;
	}
	
	private IVpnEventScoreInfo createVpnEventScoreInfo(User user, VpnScore vpnScore){
		IVpnEventScoreInfo ret = new VpnEventScoreInfo(user, vpnScore);
		
		return ret;
	}
	
	
	
	
	
	

	@Override
	public List<SeverityElement> getSeverityElements() {
		return severityOrderedList;
	}
	
	@Override
	public EBSResult getEBSAlgOnQuery(String query, int offset, int limit){
		List<Map<String, Object>> resultsMap = impalaJdbcTemplate.query(query, new ColumnMapRowMapper());
		if(resultsMap.size() == 0) {
			return new EBSResult(null, null,0, 0);
		}

		List<List<String>> listResults = new ArrayList<List<String>>((int)resultsMap.size());

		List<String> keys = new ArrayList<>(resultsMap.get(0).keySet());
		for (Map<String, Object> map : resultsMap) {
			List<String> result = new ArrayList<String>( map.size() );
			for (int i = 0; i < map.size(); i++) {
				String keyString = keys.get(i);
				Object tmp = map.get(keyString);
				if(tmp != null) {
					result.add(tmp.toString());
				} else {
					//TODO: error log.
					result.add("");
				}
			}
			listResults.add(result);
		}

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
			}
			eventMap.put(EVENT_SCORE, (double)Math.round(eventScore.score));
			eventResultList.add(eventMap);
		}
		
		return new EBSResult(eventResultList, ebsresult.global_score, offset, ebsresult.event_score_list.size());
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
}
