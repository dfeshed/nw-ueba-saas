package fortscale.ml.model.retriever;

import com.fasterxml.jackson.databind.ObjectMapper;
import fortscale.utils.logging.Logger;
import net.minidev.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

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

	public abstract Object retrieve(String contextId, Date endTime);
}
