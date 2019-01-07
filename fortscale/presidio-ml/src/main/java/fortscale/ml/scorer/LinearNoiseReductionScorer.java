package fortscale.ml.scorer;

import fortscale.common.feature.Feature;
import fortscale.domain.feature.score.CertaintyFeatureScore;
import fortscale.domain.feature.score.FeatureScore;
import fortscale.ml.model.CategoryRarityModel;
import fortscale.ml.model.ContextModel;
import fortscale.ml.model.Model;
import fortscale.ml.model.Sigmoid;
import fortscale.ml.model.cache.EventModelsCacheService;
import org.springframework.beans.factory.annotation.Configurable;
import presidio.ade.domain.record.AdeRecordReader;

import java.util.ArrayList;
import java.util.List;


/***
 * Calculate score by main scorer and inverse model scorer and reduce by reductionWeight.
 * reductionWeight defined by ratio between num of contexts with same occurrence and num of contexts in schema
 * and then mapped linearly by noiseReductionWeight. (e.g 0 -> 0.5, 1 -> 0.95)
 */
@Configurable(preConstruction = true)
public class LinearNoiseReductionScorer extends AbstractScorer {

    private Scorer mainScorer;
    private Scorer reductionScorer;
    private String occurrencesToNumOfDistinctFeatureValueModelName;
    private List<String> occurrencesToNumOfDistinctFeatureValueContextFieldNames;
    private String mainScorerModelName;
    private String mainScorerFeatureName;
    private List<String> mainScorerContextFieldNames;
    private String contextModelName;
    private List<String> contextModelContextFieldNames;
    private ScoreMapping.ScoreMappingConf noiseReductionWeight;
    private EventModelsCacheService eventModelsCacheService;
    private int maxRareCount;
    private double xWithValueHalfFactor;
    private double epsilonValueForMaxX;

    public LinearNoiseReductionScorer(
            String scorerName,
            Scorer mainScorer,
            Scorer reductionScorer,
            String occurrencesToNumOfDistinctFeatureValueModelName,
            List<String> occurrencesToNumOfDistinctFeatureValueContextFieldNames,
            String mainScorerModelName,
            String mainScorerFeatureName,
            List<String> mainScorerContextFieldNames,
            String contextModelName,
            List<String> contextModelContextFieldNames,
            ScoreMapping.ScoreMappingConf noiseReductionWeight,
            EventModelsCacheService eventModelsCacheService,
            int maxRareCount,
            double xWithValueHalfFactor,
            double epsilonValueForMaxX) {
        super(scorerName);
        this.mainScorer = mainScorer;
        this.reductionScorer = reductionScorer;
        this.noiseReductionWeight = noiseReductionWeight;
        this.occurrencesToNumOfDistinctFeatureValueModelName = occurrencesToNumOfDistinctFeatureValueModelName;
        this.occurrencesToNumOfDistinctFeatureValueContextFieldNames = occurrencesToNumOfDistinctFeatureValueContextFieldNames;
        this.mainScorerModelName = mainScorerModelName;
        this.mainScorerFeatureName = mainScorerFeatureName;
        this.mainScorerContextFieldNames = mainScorerContextFieldNames;
        this.contextModelName = contextModelName;
        this.contextModelContextFieldNames = contextModelContextFieldNames;
        this.eventModelsCacheService = eventModelsCacheService;
        this.maxRareCount = maxRareCount;
        this.xWithValueHalfFactor = xWithValueHalfFactor;
        this.epsilonValueForMaxX = epsilonValueForMaxX;
    }

    @Override
    public FeatureScore calculateScore(AdeRecordReader adeRecordReader) {
        FeatureScore featureScore = null;
        FeatureScore mainScore = mainScorer.calculateScore(adeRecordReader);
        if (mainScore != null) {
            if (mainScore.getScore() == 0) {
                // get the score from the main scorer
                featureScore = new FeatureScore(getName(), mainScore.getScore());
            } else {
                FeatureScore reducingScore = reductionScorer.calculateScore(adeRecordReader);
                if (reducingScore == null) {
                    // get the score from the main scorer
                    featureScore = new FeatureScore(getName(), mainScore.getScore());
                } else {
                    List<FeatureScore> featureScores = new ArrayList<>();
                    featureScores.add(mainScore);
                    featureScores.add(reducingScore);
                    double score = mainScore.getScore();

                    if (reducingScore.getScore() < score) {
                        Double reductionWeight = calcReductionWeight(adeRecordReader);
                        if (reductionWeight == null) return null;

                        double reducingWeightMultiplyCertainty = reductionWeight * reducingScore.getCertainty();
                        score = reducingScore.getScore() * reducingWeightMultiplyCertainty +
                                mainScore.getScore() * (1 - reducingWeightMultiplyCertainty);
                    }
                    featureScore = new FeatureScore(getName(), score, featureScores);
                }
            }
        }

        if (featureScore == null) {
            return new CertaintyFeatureScore(getName(), 0d, 0d);
        }

        return featureScore;
    }

    private Double calcReductionWeight(AdeRecordReader adeRecordReader) {
        Model mainScorerModel = getModel(adeRecordReader, mainScorerModelName, mainScorerContextFieldNames);
        Model categoryRarityGlobalModel = getModel(adeRecordReader, occurrencesToNumOfDistinctFeatureValueModelName, occurrencesToNumOfDistinctFeatureValueContextFieldNames);
        Model contextModel = getModel(adeRecordReader, contextModelName, contextModelContextFieldNames);

        Feature feature = Feature.toFeature(mainScorerFeatureName, adeRecordReader.get(mainScorerFeatureName));
        if (categoryRarityGlobalModel == null || contextModel == null || mainScorerModel == null || feature.getValue() == null) {
            return null;
        }

        String featureValue = feature.getValue().toString();
        Double count = ((CategoryRarityModel) mainScorerModel).getFeatureCount(featureValue);
        if (count == null) count = 0d;
        //todo: verify after Yaron changes
        List<Double> buckets = ((OccurrencesToNumOfDistinctFeatureValuesModel) categoryRarityGlobalModel).getOccurrencesToNumOfDistinctFeatureValuesList();
        double numOfContextsWithSameOccurrence = buckets.get((int) Math.round(count));
        for (int i = (int) Math.round(count) + 1; i < count + maxRareCount; i++) {
            double commonnessDiscount = calcCommonnessDiscounting(maxRareCount, i - count + 1);
            numOfContextsWithSameOccurrence += (buckets.get(i) - buckets.get(i - 1)) * commonnessDiscount;
        }

        long numOfContexts = ((ContextModel) contextModel).getNumOfContexts();
        double percentage = numOfContextsWithSameOccurrence / numOfContexts;
        return ScoreMapping.mapScore(percentage, noiseReductionWeight);
    }

    protected Model getModel(AdeRecordReader adeRecordReader, String modelName, List<String> contextFieldNames) {
        return eventModelsCacheService.getLatestModelBeforeEventTime(adeRecordReader, modelName, contextFieldNames);
    }

    private double calcCommonnessDiscounting(int range, double occurrence) {
        return Sigmoid.calcLogisticFunc(
                range * xWithValueHalfFactor,
                range,
                epsilonValueForMaxX,
                occurrence - 1);
    }

}
