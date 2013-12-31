package fortscale.services.fe.impl;

import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import fortscale.domain.ad.AdUser;
import fortscale.domain.ad.dao.AdUserRepository;
import fortscale.domain.core.dao.UserRepository;
import fortscale.domain.fe.IFeature;
import fortscale.domain.fe.dao.AdUsersFeaturesExtractionRepository;
import fortscale.services.UserService;
import fortscale.services.fe.FeService;
import fortscale.services.impl.ImpalaWriterFactory;
import fortscale.utils.logging.Logger;

@Service("feService")
public class FeServiceImpl implements FeService {
	private static Logger logger = Logger.getLogger(FeServiceImpl.class);
	
	
	@Autowired
	private AdUserRepository adUserRepository;
		
	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private AdUsersFeaturesExtractionRepository adUsersFeaturesExtractionRepository;
	
	@Autowired
	private UserService userService;
	
	@Autowired
	private ImpalaWriterFactory impalaGroupsScoreWriterFactory;


	@Override
	public Iterable<AdUser> getAdUsersAttrVals() {
		
//		Map<String, List<FeAttribute>> ret = new HashMap<String, List<FeAttribute>>();
//				
//		List<AdUser> adUsersAttrValsList = adUserRepository.findAdUsersAttrVals();
//		for(AdUser adUser: adUsersAttrValsList){
//			User user = userRepository.findByAdDn(adUser.getDistinguishedName());
//			if(user == null){
//				log WARNING
//				continue;
//			}
//			List<FeAttribute> attrValsList = new ArrayList<>();
//			for(Entry<String, String> entry: adUser.getAttrVals().entrySet()){
//				attrValsList.add(new FeAttribute(entry.getKey(), entry.getValue()));
//			}
//			ret.put(user.getId().toString(), attrValsList);
//		}
		Long timestampepoch = adUserRepository.getLatestTimeStampepoch();
		if(timestampepoch == null) {
			logger.error("no timestamp. probably the ad_user table is empty.");
			return Collections.emptyList();
		}
		return adUserRepository.findByTimestampepoch(timestampepoch);
	}

	@Override
	public void setAdUsersScores(Map<String, Double> userScoresMap,
			Map<String, Collection<IFeature>> userFeaturesScoresMap, Date timestamp) {
//		if(userScoresMap.size() == 0){
//			logger.warn("the collection is empty");
//			return;
//		}
//				
//		
//		double avgScore = 0;
//		for(Double score: userScoresMap.values()){
//			avgScore += score;
//		}
//		avgScore = avgScore/userScoresMap.size();
//		
//		
//		for(Entry<String, Double> ent: userScoresMap.entrySet()){
//			User user = userRepository.findByAdDn(ent.getKey());
//			if(user == null){
//				logger.error("user with distinuished name ({}) was not found", ent.getKey());
//				continue;
//			}
//			//inserting new ml scores.
//			AdUserFeaturesExtraction adUserFeaturesExtraction = new AdUserFeaturesExtraction(Classifier.ad.getId(), user.getId(), ent.getKey());
//			adUserFeaturesExtraction.setScore(ent.getValue());
//			adUserFeaturesExtraction.setTimestamp(timestamp);
//			adUserFeaturesExtraction.setAttributes(new ArrayList<>(userFeaturesScoresMap.get(ent.getKey())));
//			adUsersFeaturesExtractionRepository.saveMap(adUserFeaturesExtraction);
//			
//			//updating the user with the new score.
//			userService.updateUserScore(user, timestamp, Classifier.ad.getId(), ent.getValue(), avgScore, true, true);
//		}
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
