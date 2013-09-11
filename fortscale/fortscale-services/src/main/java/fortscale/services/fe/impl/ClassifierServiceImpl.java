package fortscale.services.fe.impl;

import java.util.ArrayList;
import java.util.Collections;
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
import fortscale.domain.fe.dao.ValueSeperator;
import fortscale.services.IClassifierScoreDistribution;
import fortscale.services.fe.Classifier;
import fortscale.services.fe.ClassifierService;
import fortscale.services.fe.ISuspiciousUserInfo;
import fortscale.services.impl.ClassifierScoreDistribution;
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
	

	
	public Classifier getClassifier(String classifierId){
		return classifiersMap.get(classifierId);
	}

	@Override
	public List<IClassifierScoreDistribution> getScoreDistribution(String classifierId) {
		List<ValueSeperator> seperators = new ArrayList<>();
		seperators.add(new ValueSeperator("All", 0));
		for(SeverityElement element: severityOrderedList){
			seperators.add(new ValueSeperator(element.getName(), element.getValue()));
		}
		seperators = adUsersFeaturesExtractionRepository.calculateNumOfUsersWithScoresGTValueSortByTimestamp(classifierId, seperators);
		
		List<IClassifierScoreDistribution> ret = new ArrayList<>();
		int total = seperators.get(0).getCount();
		int prevPercent = 0;
		int i = 0;
		for(ValueSeperator seperator: seperators){
			if(i == 0){
				i++;
				continue;
			}
			int percent = (int)((seperator.getCount()/(double)total)*100);
			ret.add(new ClassifierScoreDistribution(seperator.getName(), seperator.getCount(), percent - prevPercent));
			prevPercent = percent;
		}
		return ret;
	}

	@Override
	public List<ISuspiciousUserInfo> getSuspiciousUsers(String classifierId, String severityId) {
		Pageable pageable = new PageRequest(0, 1, Direction.DESC, AdUserFeaturesExtraction.timestampField);
		List<AdUserFeaturesExtraction> ufeList = adUsersFeaturesExtractionRepository.findByClassifierId(classifierId, pageable);
		if(ufeList == null || ufeList.size() == 0){
			return Collections.emptyList();
		}
		AdUserFeaturesExtraction ufe = ufeList.get(0);
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
		
		pageable = new PageRequest(0, 10, Direction.DESC, AdUserFeaturesExtraction.scoreField);
		ufeList = adUsersFeaturesExtractionRepository.findByClassifierIdAndTimestampAndScoreBetween(classifierId, ufe.getTimestamp(), lowestVal, upperVal, pageable);
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

	
}
