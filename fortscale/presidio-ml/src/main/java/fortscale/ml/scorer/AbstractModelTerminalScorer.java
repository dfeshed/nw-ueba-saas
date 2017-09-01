package fortscale.ml.scorer;

import fortscale.common.feature.Feature;
import fortscale.domain.feature.score.FeatureScore;
import fortscale.ml.model.Model;
import fortscale.ml.model.cache.EventModelsCacheService;
import org.apache.commons.lang.StringUtils;
import org.springframework.util.Assert;
import presidio.ade.domain.record.AdeRecordReader;

import java.util.List;

public abstract class AbstractModelTerminalScorer extends AbstractModelScorer {
    private String featureName;

    public AbstractModelTerminalScorer(String scorerName,
                                       String modelName,
                                       List<String> additionalModelNames,
                                       List<String> contextFieldNames,
                                       List<List<String>> additionalContextFieldNames,
                                       String featureName,
                                       int minNumOfSamplesToInfluence,
                                       int enoughNumOfSamplesToInfluence,
                                       boolean isUseCertaintyToCalculateScore,
                                       EventModelsCacheService eventModelsCacheService) {
        super(scorerName, modelName, additionalModelNames, contextFieldNames, additionalContextFieldNames,
                minNumOfSamplesToInfluence, enoughNumOfSamplesToInfluence, isUseCertaintyToCalculateScore, eventModelsCacheService);
        Assert.isTrue(StringUtils.isNotBlank(featureName), "feature name cannot be null empty or blank");
        this.featureName = featureName;
    }

    public String getFeatureName() {
        return featureName;
    }

    /**
     * Get the model the same way {@link AbstractModelScorer} gets it, except that the {@link Feature} object
     * is passed so that DiscreteTriggeredModelCacheManagerSamza will get updated with the feature.
     */
    protected Model getModel(AdeRecordReader adeRecordReader, String modelName, List<String> contextFieldNames) {
        return eventModelsCacheService.getModel(adeRecordReader, modelName, contextFieldNames);
    }

    private Feature getFeature(AdeRecordReader adeRecordReader) {
        return Feature.toFeature(featureName, adeRecordReader.get(featureName));
    }

    @Override
    final protected FeatureScore calculateScore(Model model,
                                                List<Model> additionalModels,
                                                AdeRecordReader adeRecordReader) {
        Feature feature = getFeature(adeRecordReader);
        if (model == null || additionalModels.contains(null) || feature == null || feature.getValue() == null) {
            //todo: add metrics.
            return null;
        }
        return new FeatureScore(getName(), calculateScore(model, additionalModels, feature));
    }

    abstract protected double calculateScore(Model model, List<Model> additionalModels, Feature feature);
}
