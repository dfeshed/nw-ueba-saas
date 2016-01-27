package fortscale.ml.scorer;

import fortscale.common.feature.Feature;
import fortscale.common.feature.FeatureNumericValue;
import fortscale.ml.model.Model;
import fortscale.ml.model.prevalance.field.ContinuousDataModel;
import fortscale.ml.scorer.algorithms.ContinuousValuesModelScorerAlgorithm;
import org.springframework.util.Assert;

import java.util.List;

public class ContinuousValuesModelScorer extends AbstractModelScorer {
	private static final String MODEL_TYPE_ERROR_MSG = String.format(
			"Model must be of type %s.", ContinuousDataModel.class.getSimpleName());
	private static final String FEATURE_VALUE_TYPE_ERROR_MSG = String.format(
			"Feature value must be of type %s.", FeatureNumericValue.class.getSimpleName());

	public ContinuousValuesModelScorer(
			String scorerName, String modelName, List<String> contextFieldNames, String featureName,
			int minNumOfSamplesToInfluence, int enoughNumOfSamplesToInfluence,
			boolean isUseCertaintyToCalculateScore) {

		super(scorerName, modelName, contextFieldNames, featureName,
				minNumOfSamplesToInfluence, enoughNumOfSamplesToInfluence,
				isUseCertaintyToCalculateScore);
	}

	@Override
	public double calculateScore(Model model, Feature feature) {
		Assert.isInstanceOf(ContinuousDataModel.class, model, MODEL_TYPE_ERROR_MSG);
		Assert.isInstanceOf(FeatureNumericValue.class, feature.getValue(), FEATURE_VALUE_TYPE_ERROR_MSG);
		double value = ((FeatureNumericValue)feature.getValue()).getValue().doubleValue();
		return ContinuousValuesModelScorerAlgorithm.calculate((ContinuousDataModel)model, value);
	}
}
