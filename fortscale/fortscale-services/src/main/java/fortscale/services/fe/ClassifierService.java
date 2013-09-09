package fortscale.services.fe;

import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Service;

@Service
public class ClassifierService {
	private Map<String, Classifier> classifiersMap = getClassifiersMap();
	
	private static Map<String, Classifier> getClassifiersMap(){
		Map<String, Classifier> ret = new HashMap<String, Classifier>();
		for(Classifier classifier: Classifier.values()){
			ret.put(classifier.getId(), classifier);
		}
		return ret;
	}
	
	public Classifier getClassifier(String classifierId){
		return classifiersMap.get(classifierId);
	}
}
