package fortscale.ml.scorer;

import fortscale.domain.feature.score.FeatureScore;
import fortscale.ml.model.Model;
import fortscale.ml.model.PersonalThresholdModel;
import fortscale.ml.model.cache.EventModelsCacheService;
import fortscale.ml.scorer.algorithms.PersonalThresholdModelScorerAlgorithm;
import fortscale.ml.scorer.config.IScorerConf;
import fortscale.ml.scorer.config.ModelScorerConf;
import fortscale.utils.factory.FactoryService;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.util.Assert;
import presidio.ade.domain.record.AdeRecordReader;

import java.util.Collections;
import java.util.List;

public class PersonalThresholdModelScorer extends AbstractModelInternalUniScorer {
	private double maxRatioFromUniformThreshold;

	public PersonalThresholdModelScorer(String scorerName,
										String modelName,
										List<String> contextFieldNames,
										IScorerConf baseScorerConf,
										double maxRatioFromUniformThreshold,
										FactoryService<Scorer> factoryService,
										EventModelsCacheService eventModelsCacheService) {
		super(scorerName, modelName, contextFieldNames, baseScorerConf,factoryService,eventModelsCacheService);
		Assert.isTrue(maxRatioFromUniformThreshold > 0, "maxRatioFromUniformThreshold must be positive");
		this.maxRatioFromUniformThreshold = maxRatioFromUniformThreshold;
	}

	@Override
	protected FeatureScore calculateScore(double baseScore,
										  Model model,
										  List<Model> additionalModels,
										  AdeRecordReader adeRecordReader) {
		Model baseScorerModel = ((AbstractModelScorer) baseScorer).getMainModel(adeRecordReader);
		double calibratedScore = PersonalThresholdModelScorerAlgorithm.calculateScore(
				baseScore,
				baseScorerModel.getNumOfSamples(),
				(PersonalThresholdModel) model,
				maxRatioFromUniformThreshold
		);
		return new FeatureScore(getName(), calibratedScore);
	}
}
