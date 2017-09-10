package fortscale.utils.ttl.store;

import fortscale.utils.ttl.record.TtlData;

import java.util.List;


public interface TtlDataStore {

    /**
     * Update or create ttlData
     *
     * @param ttlData record to save
     */
    void save(TtlData ttlData);

    /**
     * Get TtlData records by application name
     * @param appName application name
     * @return List<TtlData>
     */
    List<TtlData> getTtlDataList(String appName);
}

