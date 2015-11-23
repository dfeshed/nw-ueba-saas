package fortscale.ml.model.retriever;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "type")
@JsonSubTypes({
		@JsonSubTypes.Type(value = ContinuousDataHistogramExponentialDecay.class, name = ContinuousDataHistogramExponentialDecay.DATA_RETRIEVER_FUNCTION_TYPE)
})
public interface IDataRetrieverFunction {
	Object execute(Object data, long timeRelativeToNow);
}
