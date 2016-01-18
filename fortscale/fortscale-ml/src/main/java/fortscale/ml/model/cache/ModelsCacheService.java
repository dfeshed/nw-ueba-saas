package fortscale.ml.model.cache;

import fortscale.common.feature.Feature;
import fortscale.ml.model.Model;

import java.util.List;
import java.util.Map;

/**
 * Created by amira on 28/12/2015.
 */
public interface ModelsCacheService {

    Model getModel(Feature feature, Map<String, Feature> contextFieldNamesToValuesMap, String context, long eventEpochTime);
    void save(String modelName, Map<String, Feature> contextFieldNamesToValuesMap, List<ModelCacheInfo> modelCacheInfoList);
    void window();
    void close();
}
