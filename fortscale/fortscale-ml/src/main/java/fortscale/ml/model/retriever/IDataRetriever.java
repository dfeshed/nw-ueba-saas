package fortscale.ml.model.retriever;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

import java.util.List;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "type")
@JsonSubTypes({
		@JsonSubTypes.Type(value = EntityHistogramRetriever.class, name = EntityHistogramRetriever.DATA_RETRIEVER_TYPE)
})
public abstract class IDataRetriever {
	protected long timeRangeInSeconds;
	protected List<IDataRetrieverFunction> functions;

	public abstract Object retrieve(String contextId);
}
