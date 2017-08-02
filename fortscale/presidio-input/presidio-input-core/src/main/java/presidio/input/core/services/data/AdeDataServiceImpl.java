package presidio.input.core.services.data;

import fortscale.common.general.Schema;
import fortscale.utils.logging.Logger;
import presidio.ade.domain.record.enriched.EnrichedRecord;
import presidio.ade.domain.store.AdeDataStoreCleanupParams;
import presidio.ade.domain.store.enriched.EnrichedRecordsMetadata;
import presidio.ade.sdk.executions.common.ADEManagerSDK;

import java.time.Instant;
import java.util.List;

public class AdeDataServiceImpl implements AdeDataService {
    private static final Logger logger = Logger.getLogger(AdeDataServiceImpl.class);

    private final ADEManagerSDK adeManagerSDK;

    public AdeDataServiceImpl(ADEManagerSDK adeManagerSdk) {
        this.adeManagerSDK = adeManagerSdk;
    }

    @Override
    public void store(Schema schema, Instant startDate, Instant endDate, List<? extends EnrichedRecord> records) {
        EnrichedRecordsMetadata recordsMetaData = new EnrichedRecordsMetadata(schema.getName().toLowerCase(), startDate, endDate);
        logger.info("Calling ADE SDK store for {} records with metadata {}", records.size(), recordsMetaData);
        adeManagerSDK.store(recordsMetaData, records);
    }

    @Override
    public void cleanup(Schema schema, Instant startDate, Instant endDate) {
        AdeDataStoreCleanupParams cleanupParams = new AdeDataStoreCleanupParams(startDate, endDate, schema.getName().toLowerCase());
        logger.info("Calling ADE SDK cleanup with cleanup params {}", cleanupParams);
        adeManagerSDK.cleanup(cleanupParams);
    }
}
