package fortscale.ml.scorer;

import fortscale.ml.model.Model;

import java.util.List;

public abstract class RawNumOfSamplesCetaintyModelScorer extends AbstractModelScorer {
	public RawNumOfSamplesCetaintyModelScorer(String scorerName,
											  String modelName,
											  List<String> additionalModelNames,
											  List<String> contextFieldNames,
											  List<List<String>> additionalContextFieldNames,
											  String featureName,
											  int minNumOfSamplesToInfluence,
											  int enoughNumOfSamplesToInfluence,
											  boolean isUseCertaintyToCalculateScore) {
		super(scorerName, modelName, additionalModelNames, contextFieldNames, additionalContextFieldNames,
				featureName, minNumOfSamplesToInfluence, enoughNumOfSamplesToInfluence, isUseCertaintyToCalculateScore);
	}

	protected double calculateCertainty(Model model) {
		return model.getNumOfSamples();
	}
}
