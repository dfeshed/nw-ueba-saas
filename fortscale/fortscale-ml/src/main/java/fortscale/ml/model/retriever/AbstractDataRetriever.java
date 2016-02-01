package fortscale.ml.model.retriever;

import com.fasterxml.jackson.databind.ObjectMapper;
import fortscale.ml.model.retriever.function.IDataRetrieverFunction;
import fortscale.utils.logging.Logger;
import fortscale.utils.time.TimestampUtils;
import net.minidev.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;

public abstract class AbstractDataRetriever {
	protected static final Logger logger = Logger.getLogger(AbstractDataRetriever.class);

	protected long timeRangeInSeconds;
	protected List<IDataRetrieverFunction> functions;

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
	}

	protected Date getStartTime(Date endTime) {
		long endTimeInSeconds = TimestampUtils.convertToSeconds(endTime.getTime());
		long startTimeInSeconds = endTimeInSeconds - timeRangeInSeconds;
		return new Date(TimestampUtils.convertToMilliSeconds(startTimeInSeconds));
	}

	public abstract Object retrieve(String contextId, Date endTime);

	/**
	 * @return a Set of names as they appear in the events, of the features which are the base for the data that this
	 * retriever retrieves.
	 */
	public abstract Set<String> getEventFeatureNames();

	public abstract List<String> getContextFieldNames();
}
