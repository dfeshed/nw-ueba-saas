package fortscale.services.fe.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import fortscale.domain.ad.AdUser;
import fortscale.domain.ad.dao.AdUserRepository;
import fortscale.domain.core.ClassifierScore;
import fortscale.domain.core.ScoreInfo;
import fortscale.domain.core.User;
import fortscale.domain.core.dao.UserRepository;
import fortscale.domain.fe.AdUserFeaturesExtraction;
import fortscale.domain.fe.IFeature;
import fortscale.domain.fe.dao.AdUsersFeaturesExtractionRepository;
import fortscale.services.fe.Classifier;
import fortscale.services.fe.FeService;

@Service("feService")
public class FeServiceImpl implements FeService {
	
	@Autowired
	private AdUserRepository adUserRepository;
		
	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private AdUsersFeaturesExtractionRepository adUsersFeaturesExtractionRepository;
	
	

	@Override
	public Iterable<AdUser> getAdUsersAttrVals() {
		
//		Map<String, List<FeAttribute>> ret = new HashMap<String, List<FeAttribute>>();
//				
//		List<AdUser> adUsersAttrValsList = adUserRepository.findAdUsersAttrVals();
//		for(AdUser adUser: adUsersAttrValsList){
//			User user = userRepository.findByAdDn(adUser.getDistinguishedName());
//			if(user == null){
//				//TODO: WARNING
//				continue;
//			}
//			List<FeAttribute> attrValsList = new ArrayList<>();
//			for(Entry<String, String> entry: adUser.getAttrVals().entrySet()){
//				attrValsList.add(new FeAttribute(entry.getKey(), entry.getValue()));
//			}
//			ret.put(user.getId().toString(), attrValsList);
//		}
		String timestamp = adUserRepository.getLatestTimeStamp();
		return adUserRepository.findByTimestamp(timestamp);
	}

	@Override
	public void setAdUsersScores(Map<String, Double> userScoresMap,
			Map<String, Collection<IFeature>> userFeaturesScoresMap, Date timestamp) {
		if(userScoresMap.size() == 0){
			//TODO: WARN LOG
			return;
		}
		double avgScore = 0;
		for(Double score: userScoresMap.values()){
			avgScore += score;
		}
		avgScore = avgScore/userScoresMap.size();
		
		for(Entry<String, Double> ent: userScoresMap.entrySet()){
			User user = userRepository.findByAdDn(ent.getKey());
			if(user == null){
				//TODO: ERROR MESSAGE
				continue;
			}
			//inserting new ml scores.
			AdUserFeaturesExtraction adUserFeaturesExtraction = new AdUserFeaturesExtraction(Classifier.ad.getId(), user.getId(), ent.getKey());
			adUserFeaturesExtraction.setScore(ent.getValue());
			adUserFeaturesExtraction.setTimestamp(timestamp);
			adUserFeaturesExtraction.setAttrVals(new ArrayList<>(userFeaturesScoresMap.get(ent.getKey())));
			adUsersFeaturesExtractionRepository.saveMap(adUserFeaturesExtraction);
			
			//updating the user with the new score.
			ClassifierScore cScore = user.getScore(Classifier.ad.getId());
			if(cScore == null){
				cScore = new ClassifierScore();
				cScore.setClassifierId(Classifier.ad.getId());
			}else{
				ScoreInfo scoreInfo = new ScoreInfo();
				scoreInfo.setScore(cScore.getScore());
				scoreInfo.setAvgScore(cScore.getAvgScore());
				scoreInfo.setTimestamp(cScore.getTimestamp());
				List<ScoreInfo> prevScores = cScore.getPrevScores();
				if(prevScores.isEmpty()){
					prevScores = new ArrayList<ScoreInfo>();
				}
				prevScores.add(0, scoreInfo);
				cScore.setPrevScores(prevScores);
			}
			cScore.setScore(ent.getValue());
			cScore.setAvgScore(avgScore);
			cScore.setTimestamp(timestamp);
			user.putClassifierScore(cScore);
			userRepository.save(user);
		}
	}

//	@Override
//	public void setAdUsersFeaturesExtraction(
//			Map<String, Map<String, String>> AdUsersFeaturesExtractionMap) {
//		for(Entry<String, Map<String, String>> ent: AdUsersFeaturesExtractionMap.entrySet()){
//			AdUsersFeaturesExtraction adUsersFeaturesExtraction = new AdUsersFeaturesExtraction(ent.getKey());
//			adUsersFeaturesExtraction.setAttrVals(ent.getValue());
//			adUsersFeaturesExtractionRepository.saveMap(adUsersFeaturesExtraction);
//		}
//
//	}

}
