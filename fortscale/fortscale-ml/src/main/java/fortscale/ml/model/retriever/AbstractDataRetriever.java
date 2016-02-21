package fortscale.ml.model.retriever;

import com.fasterxml.jackson.databind.ObjectMapper;
import fortscale.common.feature.Feature;
import fortscale.ml.model.retriever.pattern.replacement.RetrieverPatternReplacement;
import fortscale.ml.model.retriever.function.IDataRetrieverFunction;
import fortscale.utils.logging.Logger;
import fortscale.utils.time.TimestampUtils;
import net.minidev.json.JSONObject;
import java.util.*;

public abstract class AbstractDataRetriever {
	protected static final Logger logger = Logger.getLogger(AbstractDataRetriever.class);

	protected long timeRangeInSeconds;
	protected List<IDataRetrieverFunction> functions;
	protected RetrieverPatternReplacement retrieverPatternReplacement;

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

		retrieverPatternReplacement = dataRetrieverConf.getRetrieverPatternReplacementConf() == null ?
				null : new RetrieverPatternReplacement(dataRetrieverConf.getRetrieverPatternReplacementConf());
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
