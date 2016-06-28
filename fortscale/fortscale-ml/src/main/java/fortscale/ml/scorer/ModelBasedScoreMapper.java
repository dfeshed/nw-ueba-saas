package fortscale.ml.scorer;

import fortscale.common.event.Event;
import fortscale.common.feature.Feature;
import fortscale.domain.core.FeatureScore;
import fortscale.ml.model.ScoreMappingModel;
import fortscale.ml.model.cache.EventModelsCacheService;
import fortscale.ml.scorer.config.IScorerConf;
import fortscale.ml.scorer.config.ScoreMapperConf;
import fortscale.ml.scorer.config.ScoreMappingConf;
import fortscale.ml.scorer.factory.ScoreMapperFactory;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.util.Assert;

import java.util.HashMap;
import java.util.List;

@Configurable(preConstruction = true)
public class ModelBasedScoreMapper extends AbstractScorer {

	private IScorerConf baseScorerConf;
	private String modelName;
	private List<String> contextFieldNames;
	private String featureName;

	@Autowired
	private ScoreMapperFactory scoreMapperFactory;

	@Autowired
	private EventModelsCacheService eventModelsCacheService;

	public ModelBasedScoreMapper(String scorerName,
								 String modelName,
								 List<String> contextFieldNames,
								 String featureName,
								 IScorerConf baseScorerConf) {
		super(scorerName);
		Assert.isTrue(StringUtils.isNotBlank(featureName), "feature name cannot be null empty or blank");
		Assert.isTrue(StringUtils.isNotBlank(modelName), "model name must be provided and cannot be empty or blank.");
		Assert.notNull(contextFieldNames);
		Assert.notNull(baseScorerConf);
		this.modelName = modelName;
		this.contextFieldNames = contextFieldNames;
		this.featureName = featureName;
		this.baseScorerConf = baseScorerConf;
	}

	@Override
	public FeatureScore calculateScore(Event eventMessage, long eventEpochTimeInSec) throws Exception {
		Feature feature = featureExtractService.extract(featureName, eventMessage);
		ScoreMappingModel model = (ScoreMappingModel)eventModelsCacheService.getModel(
				eventMessage, feature, eventEpochTimeInSec, modelName, contextFieldNames);
		Scorer scoreMapper = scoreMapperFactory.getProduct(createScoreMapperConfig(model));
		return scoreMapper.calculateScore(eventMessage, eventEpochTimeInSec);
	}

	private ScoreMapperConf createScoreMapperConfig(ScoreMappingModel model) {
		ScoreMappingConf scoreMappingConf;
		if (model != null) {
			scoreMappingConf = model.getScoreMappingConf();
		} else {
			scoreMappingConf = new ScoreMappingConf();
			scoreMappingConf.setMapping(new HashMap<Double, Double>() {{
				put(0D, 0D);
				put(100D, 0D);
			}});
		}
		return new ScoreMapperConf(getName(), baseScorerConf, scoreMappingConf);
	}
}
