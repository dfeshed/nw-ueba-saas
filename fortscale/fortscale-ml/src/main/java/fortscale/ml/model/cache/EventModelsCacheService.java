package fortscale.ml.model.cache;

import fortscale.common.event.Event;
import fortscale.common.feature.Feature;
import fortscale.ml.model.Model;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Map;

public class EventModelsCacheService {
	@Autowired
	ModelsCacheService modelsCacheService;

	public Model getModel(Event eventMessage,
						  Feature feature,
						  long eventEpochTimeInSec,
						  String modelName,
						  List<String> contextFieldNames) {
		Map<String, String> contextFieldNamesToValuesMap = resolveContext(eventMessage, contextFieldNames);
		if (isNullOrMissingValues(contextFieldNamesToValuesMap, contextFieldNames)) {
			return null;
		}
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
			if(StringUtils.isEmpty(feature)) {
				return true;
			}
		}
		return false;
	}
}
