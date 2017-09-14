package fortscale.ml.model.cache;

import fortscale.ml.model.Model;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import presidio.ade.domain.record.AdeRecordReader;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EventModelsCacheService {
	@Autowired
	private ModelsCacheService modelsCacheService;

	public Model getModel(AdeRecordReader adeRecordReader, String modelName, String contextId) {
		return modelsCacheService.getModel(modelName, contextId, adeRecordReader.getDate_time());
	}

	public Model getModel(AdeRecordReader adeRecordReader, String modelName, List<String> contextFieldNames) {
		Map<String, String> contextFieldNamesToValuesMap = resolveContext(adeRecordReader, contextFieldNames);

		if (isNullOrMissingValues(contextFieldNamesToValuesMap, contextFieldNames)) {
			return null;
		}

		return modelsCacheService.getModel(modelName, contextFieldNamesToValuesMap, adeRecordReader.getDate_time());
	}

	private Map<String, String> resolveContext(AdeRecordReader adeRecordReader, List<String> contextFieldNames) {
		Map<String, String> resolvedContext = new HashMap<>();
		contextFieldNames.forEach(contextFieldName -> resolvedContext.put(contextFieldName, adeRecordReader.getContext(contextFieldName)));
		return resolvedContext;
	}

	private boolean isNullOrMissingValues(Map<String, String> contextFieldNamesToValuesMap, List<String> contextFieldNames) {
		if (contextFieldNamesToValuesMap == null) {
			return true;
		}

		if (contextFieldNamesToValuesMap.values().size() != contextFieldNames.size()) {
			return true;
		}

		for (String feature : contextFieldNamesToValuesMap.values()) {
			if (StringUtils.isEmpty(feature)) {
				return true;
			}
		}

		return false;
	}
}
