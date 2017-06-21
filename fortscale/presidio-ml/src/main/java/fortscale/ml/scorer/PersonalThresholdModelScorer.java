package fortscale.ml.scorer;

import fortscale.domain.feature.score.FeatureScore;
import fortscale.ml.model.Model;
import fortscale.ml.model.PersonalThresholdModel;
import fortscale.ml.model.cache.EventModelsCacheService;
import fortscale.ml.scorer.algorithms.PersonalThresholdModelScorerAlgorithm;
import fortscale.ml.scorer.config.IScorerConf;
import fortscale.utils.factory.FactoryService;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.util.Assert;
import presidio.ade.domain.record.AdeRecord;

import java.util.Collections;
import java.util.List;


@Configurable(preConstruction = true)
public class PersonalThresholdModelScorer extends AbstractScorer {
	private String modelName;
	private List<String> contextFieldNames;
	private AbstractModelScorer baseScorer;
	private double maxRatioFromUniformThreshold;

	@Autowired
	private FactoryService<Scorer> factoryService;

	@Autowired
	private EventModelsCacheService eventModelsCacheService;

	public PersonalThresholdModelScorer(String scorerName,
										String modelName,
										List<String> contextFieldNames,
										IScorerConf baseScorerConf,
										double maxRatioFromUniformThreshold) {

		super(scorerName);
		Assert.isTrue(StringUtils.isNotBlank(modelName), "model name must be provided and cannot be empty or blank");
		Assert.notNull(contextFieldNames, "Context field names cannot be null");
		Assert.notNull(baseScorerConf, "Base scorer conf cannot be null");
		Assert.isTrue(maxRatioFromUniformThreshold > 0, "maxRatioFromUniformThreshold must be positive");
		this.modelName = modelName;
		this.contextFieldNames = contextFieldNames;
		baseScorer = (AbstractModelScorer) factoryService.getProduct(baseScorerConf);
		this.maxRatioFromUniformThreshold = maxRatioFromUniformThreshold;
	}

	@Override
	public FeatureScore calculateScore(AdeRecord record) {
		FeatureScore baseScore = baseScorer.calculateScore(record);
		Model baseScorerModel = baseScorer.getModel(record);
		double calibratedScore = calibrateScore(record, baseScore, baseScorerModel.getNumOfSamples());
		return new FeatureScore(getName(), calibratedScore, Collections.singletonList(baseScore));
	}

	private double calibrateScore(AdeRecord record, FeatureScore baseScore, long numOfSamples) {
		PersonalThresholdModel model = (PersonalThresholdModel) eventModelsCacheService.getModel(
				record, modelName, contextFieldNames);
		if (model == null) {
			return 0;
		}

		return PersonalThresholdModelScorerAlgorithm.calculateScore(
				baseScore.getScore(),
				numOfSamples,
				model,
				maxRatioFromUniformThreshold
		);
	}
}
