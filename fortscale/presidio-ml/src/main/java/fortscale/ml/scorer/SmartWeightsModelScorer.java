package fortscale.ml.scorer;

import fortscale.domain.feature.score.FeatureScore;
import fortscale.ml.model.Model;
import fortscale.ml.model.SmartWeightsModel;
import fortscale.ml.model.cache.EventModelsCacheService;
import fortscale.ml.scorer.algorithms.SmartWeightsScorerAlgorithm;
import org.springframework.util.Assert;
import presidio.ade.domain.record.AdeRecordReader;
import presidio.ade.domain.record.aggregated.SmartRecord;

import java.util.Collections;

public class SmartWeightsModelScorer extends AbstractScorer{
    private String modelName;
    private double fractionalPower;

    private EventModelsCacheService eventModelsCacheService;
    private SmartWeightsScorerAlgorithm smartWeightsScorerAlgorithm;


    public SmartWeightsModelScorer(String scorerName, String modelName, SmartWeightsScorerAlgorithm smartWeightsScorerAlgorithm,
                                   EventModelsCacheService eventModelsCacheService) {
        super(scorerName);
        Assert.hasText(modelName, "model name must be provided and cannot be empty or blank.");
        Assert.notNull(smartWeightsScorerAlgorithm, "smartWeightsScorerAlgorithm should not be null");
        this.modelName = modelName;
        this.eventModelsCacheService = eventModelsCacheService;
        this.smartWeightsScorerAlgorithm = smartWeightsScorerAlgorithm;
    }

    @Override
    public FeatureScore calculateScore(AdeRecordReader adeRecordReader) {
        Assert.isInstanceOf(SmartRecord.class, adeRecordReader.getAdeRecord());
        SmartRecord smartRecord = (SmartRecord) adeRecordReader.getAdeRecord();

        Model model = eventModelsCacheService.getLatestModelBeforeEventTime(adeRecordReader,modelName,Collections.emptyList());
        if(model == null){
            //todo: add metrics.
            return new FeatureScore(getName(), 0.0);
        }
        Assert.isInstanceOf(SmartWeightsModel.class,model, "smart weights model scorer expect to get SmartWeightModel Class.");
        return new FeatureScore(getName(),calculateScore(smartRecord, (SmartWeightsModel) model));
    }



    private double calculateScore(SmartRecord smartRecord, SmartWeightsModel smartWeightsModel){
        return smartWeightsScorerAlgorithm.calculateScore(smartRecord,smartWeightsModel);
    }
}
