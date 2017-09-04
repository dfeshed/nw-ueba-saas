package fortscale.utils.ttl.store;


import fortscale.utils.ttl.record.TtlData;


public interface TtlDataRecordsStore extends TtlDataRecordsReader {

    /**
     * Update or create ttlData
     *
     * @param ttlData record to save
     */
    void save(TtlData ttlData);
}

