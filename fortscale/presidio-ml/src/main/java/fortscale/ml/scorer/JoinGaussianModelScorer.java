package fortscale.ml.scorer;

import fortscale.domain.feature.score.CertaintyFeatureScore;
import fortscale.domain.feature.score.FeatureScore;
import fortscale.ml.model.*;
import fortscale.ml.model.cache.EventModelsCacheService;
import fortscale.ml.model.joiner.ContinuousModelJoiner;
import fortscale.ml.utils.MaxValuesResult;
import presidio.ade.domain.record.AdeRecordReader;

import java.util.*;


public class JoinGaussianModelScorer extends GaussianModelScorer {
    private String secondaryModelName;
    private List<String> secondaryModelContextFieldNames;
    private long partitionsResolutionInSeconds;
    private int minNumOfPartitionsToInfluence;
    private int enoughNumOfPartitionsToInfluence;
    private boolean isUseCertaintyToCalculateScore;
    private ContinuousModelJoiner continuousModelJoiner;

    public JoinGaussianModelScorer(String scorerName,
                                   String modelName,
                                   String secondaryModelName,
                                   List<String> additionalModelNames,
                                   List<String> contextFieldNames,
                                   List<String> secondaryModelContextFieldNames,
                                   List<List<String>> additionalContextFieldNames,
                                   String featureName,
                                   int minNumOfPartitionsToInfluence,
                                   int enoughNumOfPartitionsToInfluence,
                                   boolean isUseCertaintyToCalculateScore,
                                   int globalInfluence,
                                   EventModelsCacheService eventModelsCacheService,
                                   long partitionsResolutionInSeconds,
                                   ContinuousModelJoiner continuousModelJoiner) {

        super(scorerName, modelName, additionalModelNames, contextFieldNames, additionalContextFieldNames, featureName,
                minNumOfPartitionsToInfluence, enoughNumOfPartitionsToInfluence, isUseCertaintyToCalculateScore, globalInfluence, eventModelsCacheService);

        if (additionalModelNames.size() != 1) {
            throw new IllegalArgumentException(this.getClass().getSimpleName() + " expects to get one additional model name");
        }

        this.secondaryModelName = secondaryModelName;
        this.secondaryModelContextFieldNames = secondaryModelContextFieldNames;
        this.partitionsResolutionInSeconds = partitionsResolutionInSeconds;
        this.minNumOfPartitionsToInfluence = minNumOfPartitionsToInfluence;
        this.enoughNumOfPartitionsToInfluence = enoughNumOfPartitionsToInfluence;
        this.isUseCertaintyToCalculateScore = isUseCertaintyToCalculateScore;
        this.continuousModelJoiner = continuousModelJoiner;
    }

    @Override
    public FeatureScore calculateScore(AdeRecordReader adeRecordReader) {
        Model model = getMainModel(adeRecordReader);
        Model secondaryModel = getModel(adeRecordReader, secondaryModelName, secondaryModelContextFieldNames);

        MaxValuesResult maxValuesResult = continuousModelJoiner.joinModels(model, secondaryModel);
        long numOfPartitions = maxValuesResult.getMaxValues().keySet().stream().map(x -> (x / partitionsResolutionInSeconds) * partitionsResolutionInSeconds).distinct().count();
        Model joinedModel = continuousModelJoiner.createContinuousModel(maxValuesResult);

        List<Model> additionalModels = getAdditionalModels(adeRecordReader);
        FeatureScore featureScore = calculateScore(joinedModel, additionalModels, adeRecordReader);
        if (featureScore == null) {
            return new CertaintyFeatureScore(getName(), 0d, 0d);
        }
        double certainty = calculateCertainty(numOfPartitions);


        if (isUseCertaintyToCalculateScore) {
            featureScore.setScore(featureScore.getScore() * certainty);
        } else {
            featureScore = new CertaintyFeatureScore(
                    featureScore.getName(),
                    featureScore.getScore(),
                    featureScore.getFeatureScores(),
                    certainty
            );
        }
        return featureScore;
    }

    /**
     * calculate certainty
     * @param numOfPartitions numOfPartitions
     * @return certainty
     */
    private double calculateCertainty(long numOfPartitions) {
        if (enoughNumOfPartitionsToInfluence <= 1) {
            return 1;
        }
        double certainty = 0;
        if (numOfPartitions >= enoughNumOfPartitionsToInfluence) {
            certainty = 1;
        } else if (numOfPartitions >= minNumOfPartitionsToInfluence) {
            certainty = ((double) (numOfPartitions - minNumOfPartitionsToInfluence + 1)) / (enoughNumOfPartitionsToInfluence - minNumOfPartitionsToInfluence + 1);
        }
        return certainty;
    }

}
