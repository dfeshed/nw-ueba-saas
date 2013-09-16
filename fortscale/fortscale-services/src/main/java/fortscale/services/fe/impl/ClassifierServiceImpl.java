package fortscale.services.fe.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.stereotype.Service;

import fortscale.domain.core.ClassifierScore;
import fortscale.domain.core.User;
import fortscale.domain.core.dao.UserRepository;
import fortscale.domain.fe.AdUserFeaturesExtraction;
import fortscale.domain.fe.dao.AdUsersFeaturesExtractionRepository;
import fortscale.domain.fe.dao.AuthDAO;
import fortscale.domain.fe.dao.Threshold;
import fortscale.services.fe.Classifier;
import fortscale.services.fe.ClassifierService;
import fortscale.services.fe.IClassifierScoreDistribution;
import fortscale.services.fe.IScoreDistribution;
import fortscale.services.fe.ISuspiciousUserInfo;
import fortscale.services.impl.SeverityElement;

@Service("classifierService")
public class ClassifierServiceImpl implements ClassifierService {
	
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
		ret.add(new SeverityElement("Low", 5));
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
	private UserRepository userRepository;
	
	@Autowired
	private AuthDAO authDAO;

	
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
		for(Threshold threshold: thresholds){
			if(i == 0){
				i++;
				continue;
			}
			int percent = (int)((threshold.getCount()/(double)total)*100);
			int count = threshold.getCount() - prevCount;
			ret.add(new ScoreDistribution(threshold.getName(), count, percent - prevPercent));
			prevPercent = percent;
			prevCount = threshold.getCount();
		}
		return ret;
	}
	
	private void calculateNumOfUsersWithScoresGTThresholdForLastRun(String classifierId,List<Threshold> thresholds){
		if(classifierId.equals(Classifier.ad.getId())){
			adUsersFeaturesExtractionRepository.calculateNumOfUsersWithScoresGTThresholdForLastRun(classifierId, thresholds);
		} else if(classifierId.equals(Classifier.auth.getId())){
			Date lastRun = authDAO.getLastRunDate();
			for(Threshold threshold: thresholds){
				threshold.setCount(authDAO.countNumOfUsersAboveThreshold(threshold, lastRun));
			}
		}
	}

	@Override
	public List<ISuspiciousUserInfo> getSuspiciousUsers(String classifierId, String severityId) {
		List<ISuspiciousUserInfo> ret = Collections.emptyList();
		if(classifierId.equals(Classifier.ad.getId())){
			ret = getAdSuspiciousUsers(classifierId, severityId);
		} else if(classifierId.equals(Classifier.auth.getId())){
			//TODO
		}
		
		return ret;
	}
	
	private List<ISuspiciousUserInfo> getAuthSuspiciousUsers(String classifierId, String severityId) {
		Date lastRun = authDAO.getLastRunDate();
		Range severityRange = getRange(severityId);
		
		return Collections.emptyList();
	}
	
	private List<ISuspiciousUserInfo> getAdSuspiciousUsers(String classifierId, String severityId) {
		Pageable pageable = new PageRequest(0, 1, Direction.DESC, AdUserFeaturesExtraction.timestampField);
		List<AdUserFeaturesExtraction> ufeList = adUsersFeaturesExtractionRepository.findByClassifierId(classifierId, pageable);
		if(ufeList == null || ufeList.size() == 0){
			return Collections.emptyList();
		}
		AdUserFeaturesExtraction ufe = ufeList.get(0);
		Date lastRun = ufe.getTimestamp();
		
		Range severityRange = getRange(severityId);
		
		pageable = new PageRequest(0, 10, Direction.DESC, AdUserFeaturesExtraction.scoreField);
		ufeList = adUsersFeaturesExtractionRepository.findByClassifierIdAndTimestampAndScoreBetween(classifierId, lastRun, severityRange.getLowestVal(), severityRange.getUpperVal(), pageable);
		List<ISuspiciousUserInfo> ret = new ArrayList<>();
		for(AdUserFeaturesExtraction adUserFeaturesExtraction: ufeList){
			User user = userRepository.findOne(adUserFeaturesExtraction.getUserId());
			ClassifierScore classifierScore = user.getScore(classifierId);
			double trend = 0;
			if(!classifierScore.getPrevScores().isEmpty()){
				double prevScore = classifierScore.getPrevScores().get(0).getScore() + 0.00001;
				trend = (int)(((classifierScore.getScore() - prevScore) / prevScore) * 10000);
				trend = trend/100;
			}
			SuspiciousUserInfo info = new SuspiciousUserInfo(user.getAdUserPrincipalName(), (int) user.getScore(classifierId).getScore(), trend);
			ret.add(info);
		}
		return ret;
	}
	
	private Range getRange(String severityId){
		int i = 0;
		for(SeverityElement element: severityOrderedList){
			if(element.getName().equals(severityId)){
				break;
			}
			i++;
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
}
