package fortscale.ml.model.retriever;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import net.minidev.json.JSONObject;
import org.springframework.util.Assert;

import java.util.List;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "type")
@JsonSubTypes({
		@JsonSubTypes.Type(value = ContextHistogramRetrieverConf.class, name = ContextHistogramRetrieverConf.CONTEXT_HISTOGRAM_RETRIEVER_CONF)
})
public abstract class AbstractDataRetrieverConf {
	private long timeRangeInSeconds;
	private List<JSONObject> functions;

	public AbstractDataRetrieverConf(long timeRangeInSeconds, List<JSONObject> functions) {
		Assert.isTrue(timeRangeInSeconds > 0);
		Assert.notNull(functions);

		this.timeRangeInSeconds = timeRangeInSeconds;
		this.functions = functions;
	}

	public long getTimeRangeInSeconds() {
		return timeRangeInSeconds;
	}

	public List<JSONObject> getFunctionConfs() {
		return functions;
	}
}
