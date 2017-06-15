package fortscale.ml.model.builder;

import fortscale.ml.model.PersonalThresholdModel;
import fortscale.ml.model.PersonalThresholdModelBuilderData;
import org.springframework.util.Assert;

public class PersonalThresholdModelBuilder implements IModelBuilder {
	private static final String NULL_MODEL_BUILDER_DATA_ERROR_MSG = "Model builder data cannot be null.";
	private static final String MODEL_BUILDER_DATA_TYPE_ERROR_MSG = String.format(
			"Model builder data must be of type %s",
			PersonalThresholdModelBuilderData.class.getSimpleName()
	);

	@Override
	public PersonalThresholdModel build(Object modelBuilderData) {
		PersonalThresholdModelBuilderData data = castModelBuilderData(modelBuilderData);
		return new PersonalThresholdModel(
				data.getNumOfContexts(),
				data.getNumOfOrganizationScores(),
				data.getOrganizationKTopProbOfHighScore()
		);
	}

	private PersonalThresholdModelBuilderData castModelBuilderData(Object modelBuilderData) {
		Assert.notNull(modelBuilderData, NULL_MODEL_BUILDER_DATA_ERROR_MSG);
		Assert.isInstanceOf(PersonalThresholdModelBuilderData.class, modelBuilderData, MODEL_BUILDER_DATA_TYPE_ERROR_MSG);
		return (PersonalThresholdModelBuilderData) modelBuilderData;
	}
}
