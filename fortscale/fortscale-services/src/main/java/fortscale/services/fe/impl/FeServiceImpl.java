package fortscale.services.fe.impl;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import fortscale.domain.ad.AdUser;
import fortscale.domain.ad.dao.AdUserRepository;
import fortscale.domain.core.dao.UserRepository;
import fortscale.domain.fe.ADFeature;
import fortscale.domain.fe.AdUserFeaturesExtraction;
import fortscale.domain.fe.dao.AdUsersFeaturesExtractionRepository;
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
		
		return adUserRepository.findAll();
	}

	@Override
	public void setAdUsersScores(Map<String, Double> userScoresMap,
			Map<String, List<ADFeature>> userFeaturesScoresMap, Date timestamp) {
		for(Entry<String, Double> ent: userScoresMap.entrySet()){
			AdUserFeaturesExtraction adUserFeaturesExtraction = new AdUserFeaturesExtraction(ent.getKey());
			adUserFeaturesExtraction.setScore(ent.getValue());
			adUserFeaturesExtraction.setTimestamp(timestamp);
			adUserFeaturesExtraction.setAttrVals(userFeaturesScoresMap.get(ent.getKey()));
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
