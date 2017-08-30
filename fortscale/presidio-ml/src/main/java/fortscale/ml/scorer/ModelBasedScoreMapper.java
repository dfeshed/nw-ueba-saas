package fortscale.ml.scorer;

import fortscale.domain.feature.score.FeatureScore;
import fortscale.ml.model.Model;
import fortscale.ml.model.ScoreMappingModel;
import fortscale.ml.model.cache.EventModelsCacheService;
import fortscale.ml.scorer.config.IScorerConf;
import fortscale.utils.factory.FactoryService;
import org.apache.commons.lang.StringUtils;
import org.springframework.util.Assert;
import presidio.ade.domain.record.AdeRecordReader;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;

public class ModelBasedScoreMapper extends AbstractModelInternalUniScorer {
	public ModelBasedScoreMapper(String scorerName,
								 String modelName,
								 List<String> contextFieldNames,
								 IScorerConf baseScorerConf,
								 FactoryService<Scorer> factoryService,
								 EventModelsCacheService eventModelsCacheService) {
		super(scorerName, modelName, contextFieldNames, baseScorerConf,factoryService,eventModelsCacheService);
	}

	@Override
	protected FeatureScore calculateScore(double baseScore,
										  Model model,
										  List<Model> additionalModels,
										  AdeRecordReader adeRecordReader) {
		double mappedScore = ScoreMapping.mapScore(baseScore, ((ScoreMappingModel) model).getScoreMappingConf());
		return new FeatureScore(getName(), mappedScore);
	}
//	private static final ScoreMapping.ScoreMappingConf ZERO_SCORE_MAPPING_CONF = new ScoreMapping.ScoreMappingConf()
//			.setMapping(new HashMap<Double, Double>() {{
//				put(0D, 0D);
//				put(100D, 0D);
//			}});
//
//	private Scorer baseScorer;
//	private String modelName;
//	private List<String> contextFieldNames;
//	private EventModelsCacheService eventModelsCacheService;
//	private FactoryService<Scorer> factoryService;
//
//	public ModelBasedScoreMapper(String scorerName,
//								 String modelName,
//								 List<String> contextFieldNames,
//								 String featureName,
//								 IScorerConf baseScorerConf,
//								 FactoryService<Scorer> factoryService,
//								 EventModelsCacheService eventModelsCacheService) {
//
//		super(scorerName);
//		Assert.isTrue(StringUtils.isNotBlank(featureName), "feature name cannot be null empty or blank");
//		Assert.isTrue(StringUtils.isNotBlank(modelName), "model name must be provided and cannot be empty or blank.");
//		Assert.notNull(contextFieldNames, "Context field names cannot be null");
//		Assert.notNull(baseScorerConf, "Base scorer conf cannot be null");
//		this.modelName = modelName;
//		this.contextFieldNames = contextFieldNames;
//		this.factoryService = factoryService;
//		this.eventModelsCacheService = eventModelsCacheService;
//		baseScorer = this.factoryService.getProduct(baseScorerConf);
//	}
//
//	@Override
//	public FeatureScore calculateScore(AdeRecordReader adeRecordReader) {
//		FeatureScore baseScore = baseScorer.calculateScore(adeRecordReader);
//		double mappedScore = mapScore(adeRecordReader, baseScore);
//		return new FeatureScore(getName(), mappedScore, Collections.singletonList(baseScore));
//	}
//
//	private double mapScore(AdeRecordReader adeRecordReader, FeatureScore baseScore) {
//		ScoreMappingModel model = (ScoreMappingModel)eventModelsCacheService.getModel(
//				adeRecordReader, modelName, contextFieldNames);
//		return ScoreMapping.mapScore(baseScore.getScore(), createScoreMappingConf(model));
//	}
//
//	private ScoreMapping.ScoreMappingConf createScoreMappingConf(ScoreMappingModel model) {
//		ScoreMapping.ScoreMappingConf scoreMappingConf;
//		if (model != null) {
//			scoreMappingConf = model.getScoreMappingConf();
//		} else {
//			scoreMappingConf = ZERO_SCORE_MAPPING_CONF;
//		}
//		return scoreMappingConf;
//	}
}
