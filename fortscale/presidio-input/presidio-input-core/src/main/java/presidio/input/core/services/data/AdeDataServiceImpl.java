package presidio.input.core.services.data;

import fortscale.common.general.DataSource;
import fortscale.utils.logging.Logger;
import presidio.ade.domain.record.enriched.EnrichedRecord;
import presidio.ade.domain.store.AdeDataStoreCleanupParams;
import presidio.ade.domain.store.enriched.EnrichedDataStore;
import presidio.ade.domain.store.enriched.EnrichedRecordsMetadata;

import java.time.Instant;
import java.util.List;


public class AdeDataServiceImpl implements AdeDataService {

    private static final Logger logger = Logger.getLogger(AdeDataServiceImpl.class);

    private final EnrichedDataStore enrichedDataStore;

    public AdeDataServiceImpl(EnrichedDataStore enrichedDataStore) {
        this.enrichedDataStore = enrichedDataStore;
    }

    @Override
    public void store(DataSource dataSource, Instant startDate, Instant endDate, List<? extends EnrichedRecord> records) {
        EnrichedRecordsMetadata recordsMetaData = new EnrichedRecordsMetadata(dataSource.getName().toLowerCase(), startDate, endDate);
        logger.info("Calling ADE SDK store for {} records with metadata {}", records.size(), recordsMetaData);
        enrichedDataStore.store(recordsMetaData, records);
    }

    @Override
    public void cleanup(DataSource dataSource, Instant startDate, Instant endDate) {
        AdeDataStoreCleanupParams cleanupParams = new AdeDataStoreCleanupParams(startDate, endDate, dataSource.getName().toLowerCase());
        logger.info("Calling ADE SDK cleanup with cleanup params {}", cleanupParams);
        enrichedDataStore.cleanup(cleanupParams);
    }
}
