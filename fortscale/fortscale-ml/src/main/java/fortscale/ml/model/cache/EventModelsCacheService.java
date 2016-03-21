package fortscale.ml.model.cache;

import fortscale.common.event.Event;
import fortscale.common.feature.Feature;
import fortscale.common.feature.FeatureStringValue;
import fortscale.common.feature.extraction.FeatureExtractService;
import fortscale.ml.model.Model;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.HashSet;
import java.util.List;
import java.util.Map;

public class EventModelsCacheService {
	@Autowired
	ModelsCacheService modelsCacheService;

	@Autowired
	FeatureExtractService featureExtractService;

	public Model getModel(Event eventMessage,
						  long eventEpochTimeInSec,
						  String featureName,
						  String modelName,
						  List<String> contextFieldNames) {
		Map<String, String> contextFieldNamesToValuesMap = resolveContext(eventMessage, contextFieldNames);
		if (isNullOrMissingValues(contextFieldNamesToValuesMap, contextFieldNames)) {
			return null;
		}
		Feature feature = featureExtractService.extract(featureName, eventMessage);
		return modelsCacheService.getModel(feature, modelName, contextFieldNamesToValuesMap, eventEpochTimeInSec);
	}

	private Map<String, String> resolveContext(Event eventMessage, List<String> contextFieldNames){
		return eventMessage.getContextFields(contextFieldNames);
	}

	private boolean isNullOrMissingValues(Map<String, String> contextFieldNamesToValuesMap, List<String> contextFieldNames) {
		if(contextFieldNamesToValuesMap==null) {
			return true;
		}
		if(contextFieldNamesToValuesMap.values().size()!=contextFieldNames.size()) {
			return true;
		}
		for(String feature: contextFieldNamesToValuesMap.values()) {
			if(feature==null || StringUtils.isEmpty(feature)) {
				return true;
			}
		}
		return false;
	}
}
