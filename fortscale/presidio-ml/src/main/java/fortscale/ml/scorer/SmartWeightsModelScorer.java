package fortscale.ml.scorer;

import fortscale.domain.feature.score.FeatureScore;
import fortscale.ml.model.Model;
import fortscale.ml.model.SmartWeightsModel;
import fortscale.ml.model.cache.EventModelsCacheService;
import org.springframework.util.Assert;
import presidio.ade.domain.record.AdeRecordReader;
import presidio.ade.domain.record.aggregated.SmartRecord;

import java.util.Collections;

public class SmartWeightsModelScorer extends AbstractScorer{
    private String modelName;

    private EventModelsCacheService eventModelsCacheService;


    public SmartWeightsModelScorer(String scorerName, String modelName,
                                   EventModelsCacheService eventModelsCacheService) {
        super(scorerName);
        Assert.hasText(modelName, "model name must be provided and cannot be empty or blank.");
        this.modelName = modelName;
    }

    @Override
    public FeatureScore calculateScore(AdeRecordReader adeRecordReader) {
        Assert.isInstanceOf(SmartRecord.class, adeRecordReader.getAdeRecord());
        SmartRecord smartRecord = (SmartRecord) adeRecordReader.getAdeRecord();

        Model model = eventModelsCacheService.getModel(adeRecordReader,modelName,Collections.emptyList());
        if(model == null){
            //todo: add metrics.
            return new FeatureScore(getName(), 0.0);
        }
        Assert.isInstanceOf(SmartWeightsModel.class, "smart weights model scorer expect to get SmartWeightModel Class.");
        return new FeatureScore(getName(),calculateScore(smartRecord, (SmartWeightsModel) model));
    }

    private double calculateScore(SmartRecord smartRecord, SmartWeightsModel smartWeightsModel){
        //todo: implement it
        return 0;
    }
}
