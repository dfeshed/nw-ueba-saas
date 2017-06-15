package fortscale.ml.model.retriever.function;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

import java.util.Date;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "type")
@JsonSubTypes({
		@JsonSubTypes.Type(value = ContinuousDataHistogramExponentialDecay.class, name = ContinuousDataHistogramExponentialDecay.DATA_RETRIEVER_FUNCTION_TYPE),
		@JsonSubTypes.Type(value = DiscreteDataHistogramIgnorePattern.class, name = DiscreteDataHistogramIgnorePattern.DATA_RETRIEVER_FUNCTION_TYPE)
})
public interface IDataRetrieverFunction {
	Object execute(Object data, Date dataTime, Date currentTime);
}
