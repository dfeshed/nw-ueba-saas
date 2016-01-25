package fortscale.ml.model.cache;

import fortscale.ml.model.store.ModelDAO;

/**
 * Created by amira on 28/12/2015.
 */
public class ModelCacheInfo {
    private ModelDAO modelDAO;
    private long lastModelLoadAttemptTimeSeconds;
    private long lastUsageTime;

    public ModelCacheInfo(ModelDAO modelDAO) {
        this.modelDAO = modelDAO;
        this.lastModelLoadAttemptTimeSeconds = System.currentTimeMillis()/1000;
    }

    public ModelDAO getModelDAO() {
        return modelDAO;
    }


    public long getLastModelLoadAttemptTimeSeconds() {
        return lastModelLoadAttemptTimeSeconds;
    }

    public void setLastModelLoadAttemptTimeSeconds(long lastModelLoadAttemptTimeSeconds) {
        this.lastModelLoadAttemptTimeSeconds = lastModelLoadAttemptTimeSeconds;
    }

    public long getLastUsageTime() {
        return lastUsageTime;
    }

    public void setLastUsageTime(long lastUsageTime) {
        this.lastUsageTime = lastUsageTime;
    }
}
