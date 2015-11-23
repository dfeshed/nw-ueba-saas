package fortscale.ml.model.retriever;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import fortscale.ml.model.data.type.IData;
import org.joda.time.DateTime;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "type")
@JsonSubTypes({
		@JsonSubTypes.Type(value = ContinuousDataHistogramExponentialDecay.class, name = ContinuousDataHistogramExponentialDecay.DATA_RETRIEVER_FUNCTION_TYPE)
})
public interface IDataRetrieverFunction {
	Object execute(IData data, DateTime currentTime);
}
