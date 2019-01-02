package fortscale.ml.scorer;

import fortscale.common.feature.Feature;
import fortscale.domain.feature.score.FeatureScore;
import fortscale.ml.model.CategoryRarityModel;
import fortscale.ml.model.ContextModel;
import fortscale.ml.model.Model;
import fortscale.ml.model.cache.EventModelsCacheService;
import org.springframework.beans.factory.annotation.Configurable;
import presidio.ade.domain.record.AdeRecordReader;

import java.util.ArrayList;
import java.util.List;


/***
 * Calculate main score and inverse model score and reduce the score by reductionWeight.
 * reductionWeight defined by ratio between num of contexts with same occurrence and num of contexts in schema
 * and then mapped linearly by noiseReductionWeight. (e.g 0 -> 0.5, 1 -> 0.95)
 */
@Configurable(preConstruction = true)
public class LinearNoiseReductionScorer extends AbstractModelScorer {

    private Scorer mainScorer;
    private Scorer reductionScorer;
    private String reductionModelName;
    private String reductionFeatureName;
    private List<String> reductionContextFieldNames;
    private ScoreMapping.ScoreMappingConf noiseReductionWeight;


    public LinearNoiseReductionScorer(
            String scorerName, Scorer mainScorer, Scorer reductionScorer,
            String modelName, List<String> contextFieldNames,
            String reductionModelName,
            String reductionFeatureName,
            List<String> reductionContextFieldNames,
            List<String> additionalModelNames,
            List<List<String>> additionalContextFieldNames,
            ScoreMapping.ScoreMappingConf noiseReductionWeight,
            int minNumOfPartitionsToInfluence,
            int enoughNumOfPartitionsToInfluence,
            boolean isUseCertaintyToCalculateScore,
            EventModelsCacheService eventModelsCacheService) {
        super(scorerName, modelName, additionalModelNames, contextFieldNames, additionalContextFieldNames,
                minNumOfPartitionsToInfluence, enoughNumOfPartitionsToInfluence,
                isUseCertaintyToCalculateScore, eventModelsCacheService);
        this.mainScorer = mainScorer;
        this.reductionScorer = reductionScorer;
        this.noiseReductionWeight = noiseReductionWeight;
        this.reductionModelName = reductionModelName;
        this.reductionFeatureName = reductionFeatureName;
        this.reductionContextFieldNames = reductionContextFieldNames;
    }


    @Override
    protected FeatureScore calculateScore(Model model, List<Model> additionalModels, AdeRecordReader adeRecordReader) {
        Feature feature = Feature.toFeature(reductionFeatureName, adeRecordReader.get(reductionFeatureName));
        if (model == null || additionalModels.contains(null) || feature.getValue() == null) {
            return null;
        }

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
                        Double reductionWeight = calcReductionWeight(model, additionalModels, adeRecordReader, feature);
                        if(reductionWeight == null) return null;

                        double reducingWeightMultiplyCertainty = reductionWeight * reducingScore.getCertainty();
                        score = reducingScore.getScore() * reducingWeightMultiplyCertainty +
                                mainScore.getScore() * (1 - reducingWeightMultiplyCertainty);
                    }
                    featureScore = new FeatureScore(getName(), score, featureScores);
                }
            }
        }
        return featureScore;
    }

    private Double calcReductionWeight(Model model, List<Model> additionalModels, AdeRecordReader adeRecordReader, Feature feature) {
        double numOfContextsWithSameOccurrence = 1;
        CategoryRarityModel reductionModel = (CategoryRarityModel) getModel(adeRecordReader, reductionModelName, reductionContextFieldNames);
        String featureValue = feature.getValue().toString();
        Double count = reductionModel.getFeatureCount(featureValue);
        if (count == null) count = 0d;
        //todo: uncomment after Yaron changes
//      List<Double> buckets = model.getOccurrencesToNumOfFeatures(count);
//      numOfContextsWithSameOccurrence += buckets.get((int)( count - 1));

        ContextModel contextModel = extractContextModel(additionalModels);
        if(contextModel == null) return null;

        int numOfContexts = contextModel.getNumOfContexts();
        double percentage = numOfContextsWithSameOccurrence / numOfContexts;
        return ScoreMapping.mapScore(percentage, noiseReductionWeight);
    }

    private ContextModel extractContextModel(List<Model> additionalModels) {
        if (additionalModels.isEmpty()) {
            return null;
        } else {
            Model additionalModel = additionalModels.get(0);
            return (ContextModel) additionalModel;
        }
    }

}
