package fortscale.ml.scorer;

import fortscale.domain.feature.score.FeatureScore;
import fortscale.ml.model.Model;
import fortscale.ml.model.SmartWeightsModel;
import fortscale.ml.model.cache.EventModelsCacheService;
import fortscale.ml.model.store.ModelDAO;
import fortscale.ml.scorer.algorithms.SmartWeightsScorerAlgorithm;
import org.springframework.util.Assert;
import presidio.ade.domain.record.AdeRecordReader;
import presidio.ade.domain.record.aggregated.SmartRecord;

import java.time.Instant;
import java.util.Collections;
import java.util.List;

public class SmartWeightsModelScorer extends AbstractScorer{
    private String modelName;

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
        Assert.isInstanceOf(SmartWeightsModel.class, model, "smart weights model scorer expect to get SmartWeightModel Class.");
        return calculateScore(smartRecord, (SmartWeightsModel)model);
    }

    public FeatureScore calculateScore(AdeRecordReader adeRecordReader, Instant modelEndTime) {
        Assert.isInstanceOf(SmartRecord.class, adeRecordReader.getAdeRecord());
        SmartRecord smartRecord = (SmartRecord) adeRecordReader.getAdeRecord();

        List<ModelDAO> modelDAOs = eventModelsCacheService.getModelDAOsSortedByEndTimeDesc(adeRecordReader,modelName,null);
        Model model = null;
        for(ModelDAO modelDAO: modelDAOs){
            if(modelDAO.getEndTime().equals(modelEndTime)){
                model = modelDAO.getModel();
            }
        }
        if(model == null){
            //todo: add metrics.
            return new FeatureScore(getName(), 0.0);
        }
        Assert.isInstanceOf(SmartWeightsModel.class, model, "smart weights model scorer expect to get SmartWeightModel Class.");
        return calculateScore(smartRecord, (SmartWeightsModel)model);
    }


    private FeatureScore calculateScore(SmartRecord smartRecord, SmartWeightsModel smartWeightsModel) {
        FeatureScore featureScore = smartWeightsScorerAlgorithm.calculateScore(smartRecord, smartWeightsModel);
        featureScore.setName(getName());
        return featureScore;
    }
}
