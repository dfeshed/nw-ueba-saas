package fortscale.ml.model.builder;

import fortscale.ml.model.PersonalThresholdModel;
import org.springframework.util.Assert;

public class PersonalThresholdModelBuilder implements IModelBuilder {
	private static final String NULL_MODEL_BUILDER_DATA_ERROR_MSG = "Model builder data cannot be null.";
	private static final String MODEL_BUILDER_DATA_TYPE_ERROR_MSG = String.format(
			"Model builder data must be of type %s",
			Integer.class.getSimpleName()
	);

	private int desiredNumOfIndicators;

	public PersonalThresholdModelBuilder(int desiredNumOfIndicators) {
		Assert.isTrue(desiredNumOfIndicators > 0);
		this.desiredNumOfIndicators = desiredNumOfIndicators;
	}

	@Override
	public PersonalThresholdModel build(Object modelBuilderData) {
		Integer numOfContexts = castModelBuilderData(modelBuilderData);
		return new PersonalThresholdModel(numOfContexts, desiredNumOfIndicators);
	}

	private Integer castModelBuilderData(Object modelBuilderData) {
		Assert.notNull(modelBuilderData, NULL_MODEL_BUILDER_DATA_ERROR_MSG);
		Assert.isInstanceOf(Integer.class, modelBuilderData, MODEL_BUILDER_DATA_TYPE_ERROR_MSG);
		return (Integer) modelBuilderData;
	}
}
