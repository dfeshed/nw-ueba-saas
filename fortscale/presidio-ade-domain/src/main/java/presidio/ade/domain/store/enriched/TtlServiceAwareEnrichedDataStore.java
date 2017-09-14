package presidio.ade.domain.store.enriched;

import fortscale.utils.ttl.TtlServiceAware;
import presidio.ade.domain.record.enriched.EnrichedRecord;
import presidio.ade.domain.store.AdeDataStoreCleanupParams;

import java.util.List;

/**
 * ADE enriched data CRUD operations.
 *
 * Created by barak_schuster on 5/21/17.
 */
public interface TtlServiceAwareEnrichedDataStore extends EnrichedDataStore, TtlServiceAware {

}
