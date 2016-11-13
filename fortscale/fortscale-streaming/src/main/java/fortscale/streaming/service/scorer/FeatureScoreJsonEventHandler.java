package fortscale.streaming.service.scorer;

import fortscale.domain.core.EntityEvent;
import fortscale.domain.core.FeatureScore;
import net.minidev.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import java.util.List;
import java.util.Map;

public class FeatureScoreJsonEventHandler {

    @Value("${fortscale.smart.f.field.featurescores}")
    private String featureScoresKey;

    @Autowired
    private FeatureScoreJsonEventConfService featureScoreJsonEventConfService;

    public void updateEventWithScoreInfo(JSONObject event, List<FeatureScore> featureScoreList) {
        for(FeatureScore featureScoreRoot: featureScoreList){
            for(Map.Entry<String,List<String>> scoreConf: featureScoreJsonEventConfService.getEventFieldNameToScorerPathMap(featureScoreRoot.getName()).entrySet()){
                updateEventWithScoreInfo(event, scoreConf.getKey(), featureScoreRoot, scoreConf.getValue());
            }
        }
        event.put(featureScoresKey, featureScoreList);
    }

    private void updateEventWithScoreInfo(JSONObject event, String eventFieldName, FeatureScore featureScoreRoot, List<String> scorePath){
        FeatureScore featureScoreOutput = featureScoreRoot;
        for(String scorerName: scorePath.subList(1,scorePath.size())){
            featureScoreOutput = featureScoreOutput.getFeatureScore(scorerName);
            if(featureScoreOutput == null){
                break;
            }
        }

        if(featureScoreOutput != null){
            //TODO: all scores should be saved in full precision (without rounding)
            double score = featureScoreOutput.getScore();
            event.put(eventFieldName, EntityEvent.ENTITY_EVENT_UNREDUCED_SCORE_FIELD_NAME.equals(eventFieldName) ?
                    score :
                    Math.round(score));
        }
    }
}
