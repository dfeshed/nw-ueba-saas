package fortscale.ml.model.builder;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonTypeName;

@JsonTypeName(ContinuousHistogramModelBuilder.MODEL_BUILDER_TYPE)
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY, getterVisibility = JsonAutoDetect.Visibility.NONE, setterVisibility = JsonAutoDetect.Visibility.NONE)
public class ContinuousHistogramModelBuilder implements IModelBuilder {
    final static String MODEL_BUILDER_TYPE = "continuous_data_histogram";
}
