package presidio.ade.domain.store.smart;

import fortscale.utils.store.record.StoreMetadataProperties;
import presidio.ade.domain.record.aggregated.SmartRecord;

import java.util.Collection;

/**
 * Exposes the methods that read from and write to the {@link SmartRecord}s store.
 *
 * @author Lior Govrin
 */
public interface SmartDataStore extends SmartDataReader {
	void storeSmartRecords(String smartRecordConfName, Collection<SmartRecord> smartRecords, StoreMetadataProperties storeMetadataProperties);
}
