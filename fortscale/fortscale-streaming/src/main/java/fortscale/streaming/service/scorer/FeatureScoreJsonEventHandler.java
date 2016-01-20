package fortscale.streaming.service.scorer;

import fortscale.ml.scorer.FeatureScore;
import net.minidev.json.JSONObject;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class FeatureScoreJsonEventHandler {
    public JSONObject updateEventWithScoreInfo(JSONObject event, List<FeatureScore> featureScore) {
        return null;
        // TODO
    }
}
