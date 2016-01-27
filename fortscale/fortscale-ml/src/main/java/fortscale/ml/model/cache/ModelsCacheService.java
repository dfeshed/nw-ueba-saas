package fortscale.ml.model.cache;

import fortscale.common.feature.Feature;
import fortscale.ml.model.Model;
import fortscale.ml.model.cache.ModelCacheInfo;

import java.util.List;

/**
 * Created by amira on 28/12/2015.
 */
public interface ModelsCacheService {

    Model getModel(Feature feature, String modelName, String context, long eventEpochTime);
    void save(String modelName, String context, List<ModelCacheInfo> modelCacheInfoList);
    void window();
    void close();
}
