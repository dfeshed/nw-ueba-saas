package fortscale.ml.model.retriever;

import com.fasterxml.jackson.databind.ObjectMapper;
import fortscale.common.feature.Feature;
import fortscale.ml.model.retriever.function.IDataRetrieverFunction;
import fortscale.ml.model.retriever.pattern.replacement.PatternReplacement;
import fortscale.utils.logging.Logger;
import fortscale.utils.time.TimestampUtils;
import net.minidev.json.JSONObject;
import java.util.*;

public abstract class AbstractDataRetriever {
	protected static final Logger logger = Logger.getLogger(AbstractDataRetriever.class);

	protected long timeRangeInSeconds;
	protected List<IDataRetrieverFunction> functions;
	protected PatternReplacement patternReplacement;

	public AbstractDataRetriever(AbstractDataRetrieverConf dataRetrieverConf) {
		timeRangeInSeconds = dataRetrieverConf.getTimeRangeInSeconds();

		ObjectMapper objectMapper = new ObjectMapper();
		functions = new ArrayList<>();

		for (JSONObject functionConf : dataRetrieverConf.getFunctionConfs()) {
			String functionConfAsString = functionConf.toJSONString();

			try {
				IDataRetrieverFunction function = objectMapper.readValue(functionConfAsString, IDataRetrieverFunction.class);
				functions.add(function);
			} catch (Exception e) {
				logger.error(String.format("Could not deserialize function JSON %s", functionConfAsString), e);
			}
		}

		patternReplacement = dataRetrieverConf.getPatternReplacementConf() == null ?
				null : new PatternReplacement(dataRetrieverConf.getPatternReplacementConf());
	}

	public String replacePattern(String original) {
		if (patternReplacement != null) {
			return patternReplacement.replacePattern(original);
		} else {
			return original;
		}
	}

	protected Date getStartTime(Date endTime) {
		long endTimeInSeconds = TimestampUtils.convertToSeconds(endTime.getTime());
		long startTimeInSeconds = endTimeInSeconds - timeRangeInSeconds;
		return new Date(TimestampUtils.convertToMilliSeconds(startTimeInSeconds));
	}

	public abstract Object retrieve(String contextId, Date endTime);
	public abstract Object retrieve(String contextId, Date endTime, Feature feature);
	public abstract String getContextId(Map<String, String> context);
	public abstract Set<String> getEventFeatureNames();
	public abstract List<String> getContextFieldNames();
}
