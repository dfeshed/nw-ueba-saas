package fortscale.ml.model.builder;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.util.Assert;

@JsonAutoDetect(fieldVisibility = Visibility.NONE, getterVisibility = Visibility.ANY, setterVisibility = Visibility.ANY)
public class PersonalThresholdModelBuilderConf implements IModelBuilderConf {
	public static final String PERSONAL_THRESHOLD_MODEL_BUILDER = "personal_threshold_model_builder";

	private int desiredNumOfIndicators;

	@JsonCreator
	public PersonalThresholdModelBuilderConf(@JsonProperty("desiredNumOfIndicators") int desiredNumOfIndicators) {
		setDesiredNumOfIndicators(desiredNumOfIndicators);
	}

	@Override
	public String getFactoryName() {
		return PERSONAL_THRESHOLD_MODEL_BUILDER;
	}

	private void setDesiredNumOfIndicators(int desiredNumOfIndicators) {
		Assert.isTrue(desiredNumOfIndicators > 0, "desiredNumOfIndicators is mandatory and must be a positive int.");
		this.desiredNumOfIndicators = desiredNumOfIndicators;
	}

	public int getDesiredNumOfIndicators() {
		return desiredNumOfIndicators;
	}
}
