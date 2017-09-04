package fortscale.utils.ttl.store;


import fortscale.utils.ttl.record.TtlData;

import java.util.List;


public interface TtlDataRecordsReader {

    /**
     * Get TtlData records by application name
     * @param appName application name
     * @return List<TtlData>
     */
    List<TtlData> findTtlData(String appName);

}

