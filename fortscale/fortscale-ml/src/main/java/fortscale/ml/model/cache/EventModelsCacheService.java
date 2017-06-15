package fortscale.ml.model.cache;

import fortscale.ml.model.Model;
import fortscale.utils.factory.FactoryService;
import fortscale.utils.recordreader.RecordReader;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import presidio.ade.domain.record.AdeRecord;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EventModelsCacheService {
	@Autowired
	private ModelsCacheService modelsCacheService;
	@Autowired
	private FactoryService<RecordReader<AdeRecord>> recordReaderFactoryService;

	public Model getModel(AdeRecord record, String modelName, List<String> contextFieldNames) {
		Map<String, String> contextFieldNamesToValuesMap = resolveContext(record, contextFieldNames);
		if (isNullOrMissingValues(contextFieldNamesToValuesMap, contextFieldNames)) {
			return null;
		}
		return modelsCacheService.getModel(modelName, contextFieldNamesToValuesMap, record.getDate_time());
	}

	private Map<String, String> resolveContext(AdeRecord record, List<String> contextFieldNames) {
		RecordReader<AdeRecord> recordReader = recordReaderFactoryService.getDefaultProduct(record.getAdeRecordType());
		Map<String, String> resolvedContext = new HashMap<>();
		contextFieldNames.forEach(contextFieldName -> resolvedContext.put(contextFieldName, recordReader.get(record, contextFieldName, String.class)));
		return resolvedContext;
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
