package fortscale.ml.scorer;

import fortscale.common.feature.Feature;
import fortscale.common.feature.FeatureNumericValue;
import fortscale.ml.model.ContinuousDataModel;
import fortscale.ml.model.Model;
import fortscale.ml.scorer.algorithms.ContinuousValuesModelScorerAlgorithm;
import fortscale.ml.scorer.config.QuadPolyCalibrationConf;
import org.springframework.util.Assert;

import java.util.List;

public class ContinuousValuesModelScorer extends AbstractModelScorer {
	private static final String MODEL_TYPE_ERROR_MSG = String.format(
			"Model must be of type %s.", ContinuousDataModel.class.getSimpleName());
	private static final String FEATURE_NULL_ERROR_MSG = "Feature cannot be null.";
	private static final String FEATURE_VALUE_TYPE_ERROR_MSG = String.format(
			"Feature value must be of type %s.", FeatureNumericValue.class.getSimpleName());

	private QuadPolyCalibration quadPolyCalibration;

	public ContinuousValuesModelScorer(
			String scorerName, String modelName, List<String> additionalModelNames,
			List<String> contextFieldNames, List<List<String>> additionalContextFieldNames, String featureName,
			int minNumOfSamplesToInfluence, int enoughNumOfSamplesToInfluence,
			boolean isUseCertaintyToCalculateScore,
			QuadPolyCalibrationConf quadPolyCalibrationConf) {

		super(scorerName, modelName, additionalModelNames, contextFieldNames, additionalContextFieldNames, featureName,
				minNumOfSamplesToInfluence, enoughNumOfSamplesToInfluence,
				isUseCertaintyToCalculateScore);

		Assert.notNull(quadPolyCalibrationConf);
		quadPolyCalibration = new QuadPolyCalibration(quadPolyCalibrationConf);
	}

	@Override
	public double calculateScore(Model model, List<Model> additionalModels, Feature feature) {
		Assert.isInstanceOf(ContinuousDataModel.class, model, MODEL_TYPE_ERROR_MSG);
		Assert.isTrue(additionalModels.size() == 0,
				this.getClass().getSimpleName() + " doesn't expect to get additional models");
		Assert.notNull(feature, FEATURE_NULL_ERROR_MSG);
		Assert.isInstanceOf(FeatureNumericValue.class, feature.getValue(), FEATURE_VALUE_TYPE_ERROR_MSG);

		double value = ((FeatureNumericValue)feature.getValue()).getValue().doubleValue();
		double score = ContinuousValuesModelScorerAlgorithm.calculateScore(value, (ContinuousDataModel)model);
		return quadPolyCalibration.calibrateScore(score);
	}
}
