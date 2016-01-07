package fortscale.ml.scorer;

import fortscale.ml.service.ModelsCacheService;
import fortscale.ml.scorer.FeatureScore;
import net.minidev.json.JSONObject;

/**
 * Created by amira on 28/12/2015.
 */
public class ScorersService {

    private final ModelsCacheService modelsCacheService;

    public ScorersService(ModelsCacheService modelsCacheService) {
        this.modelsCacheService = modelsCacheService;
    }


    public FeatureScore calculateFeatureScore(JSONObject event, long eventEpochTimeInSec, String dataSource) {
        //TODO
        return null;
    }

    public void window() {
        //TODO
    }

    public void close() {
        //TODO
    }
}
