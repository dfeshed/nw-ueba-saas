package fortscale.ml.scorer;


import fortscale.domain.feature.score.CertaintyFeatureScore;
import fortscale.domain.feature.score.FeatureScore;
import fortscale.ml.model.Model;
import fortscale.ml.model.cache.EventModelsCacheService;
import fortscale.ml.scorer.config.IScorerConf;
import fortscale.utils.factory.FactoryService;
import org.springframework.util.Assert;
import presidio.ade.domain.record.AdeRecordReader;

import java.util.Collections;
import java.util.List;


public abstract class AbstractModelInternalUniScorer extends AbstractModelScorer {
    protected Scorer baseScorer;

    public AbstractModelInternalUniScorer(String scorerName,
                                          String modelName,
                                          List<String> additionalModelNames,
                                          List<String> contextFieldNames,
                                          List<List<String>> additionalContextFieldNames,
                                          IScorerConf baseScorerConf,
                                          int minNumOfPartitionsToInfluence,
                                          int enoughNumOfPartitionsToInfluence,
                                          boolean isUseCertaintyToCalculateScore,
                                          FactoryService<Scorer> factoryService,
                                          EventModelsCacheService eventModelsCacheService) {
        super(scorerName, modelName, additionalModelNames, contextFieldNames, additionalContextFieldNames,
                minNumOfPartitionsToInfluence, enoughNumOfPartitionsToInfluence, isUseCertaintyToCalculateScore, eventModelsCacheService);
        Assert.notNull(baseScorerConf, "base scorer should not be null");
        Assert.notNull(factoryService, "factory service should not be null");
        baseScorer = factoryService.getProduct(baseScorerConf);
    }


    public AbstractModelInternalUniScorer(String scorerName,
                                          String modelName,
                                          List<String> contextFieldNames,
                                          IScorerConf baseScorerConf,
                                          int minNumOfPartitionsToInfluence,
                                          int enoughNumOfPartitionsInfluence,
                                          boolean isUseCertaintyToCalculateScore,
                                          FactoryService<Scorer> factoryService,
                                          EventModelsCacheService eventModelsCacheService) {
        this(scorerName, modelName, null, contextFieldNames, null, baseScorerConf, minNumOfPartitionsToInfluence,
                enoughNumOfPartitionsInfluence, isUseCertaintyToCalculateScore, factoryService, eventModelsCacheService);
    }

    public AbstractModelInternalUniScorer(String scorerName,
                                          String modelName,
                                          List<String> contextFieldNames,
                                          IScorerConf baseScorerConf,
                                          FactoryService<Scorer> factoryService,
                                          EventModelsCacheService eventModelsCacheService) {
        this(scorerName, modelName, contextFieldNames, baseScorerConf, 1, 1, false, factoryService, eventModelsCacheService);
    }

    @Override
    final protected FeatureScore calculateScore(Model model,
                                                List<Model> additionalModels,
                                                AdeRecordReader adeRecordReader){
        FeatureScore baseScore = baseScorer.calculateScore(adeRecordReader);
        List<FeatureScore> baseFeatureScores = Collections.singletonList(baseScore);
        if (model == null || additionalModels.contains(null)) {
            return new CertaintyFeatureScore(getName(), 0.0, baseFeatureScores, 0.0);
        }
        FeatureScore featureScore = calculateScore(baseScore.getScore(), model, additionalModels, adeRecordReader);
        featureScore.setFeatureScores(baseFeatureScores);
        return featureScore;
    }

    /**
     * @param baseScore the score returned by the wrapped scorer.
     * @param model the model used by this scorer to give a score. model is never null.
     * @param additionalModels additional models used by this scorer to give a score. additionalModels never contain null.
     */
    protected abstract FeatureScore calculateScore(double baseScore,
                                                   Model model,
                                                   List<Model> additionalModels,
                                                   AdeRecordReader adeRecordReader);
}
