package fortscale.ml.model.builder;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;

@JsonAutoDetect(fieldVisibility = Visibility.NONE, getterVisibility = Visibility.ANY, setterVisibility = Visibility.ANY)
public class PersonalThresholdModelBuilderConf implements IModelBuilderConf {
	public static final String PERSONAL_THRESHOLD_MODEL_BUILDER = "personal_threshold_model_builder";

	@Override
	public String getFactoryName() {
		return PERSONAL_THRESHOLD_MODEL_BUILDER;
	}
}
