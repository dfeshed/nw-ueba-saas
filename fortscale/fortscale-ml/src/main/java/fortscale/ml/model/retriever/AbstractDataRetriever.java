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

	/**
	 * @return the names of the features that should be scored by the model built by this retriever.
	 * 		   In the case where the model is used inside the "additional-models" section of the scorer ASL
	 * 		   (and not as the main "model"), this function won't be called (so it's good to throw an
	 * 		   UnsupportedOperationException so no one will accidentally use the model as the main model).
	 */
	public abstract Set<String> getEventFeatureNames();

	/**
	 * @return the names of the fields which together identify the entity being modeled, e.g. "normalized_username".
	 * 		   In the case where the model built by this retriever is a global model (which means there's only one
	 * 		   model instance for the whole organization - which is achieved by not specifying "selector" in the
	 * 		   model building ASL), this method should return an empty list.
	 */
	public abstract List<String> getContextFieldNames();

	/**
	 * @param context a mapping from context field name to its value. The context field names are the result
	 *                of getContextFieldNames(). The values are the the values of these fields in the entity
	 *                being modeled.
	 * @return the context id of the entity being modeled. In the case where the model built by this retriever
	 * 		   is a global model (which means there's only one model instance for the whole organization - which
	 * 		   is achieved by not specifying "selector" in the model building ASL), this function should return null.
	 */
	public abstract String getContextId(Map<String, String> context);
}
