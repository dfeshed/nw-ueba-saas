package presidio.input.core.services.data;

import fortscale.common.general.Schema;
import presidio.ade.domain.record.enriched.EnrichedRecord;

import java.time.Instant;
import java.util.List;

public interface AdeDataService {

    /**
     * stores the given records
     *
     * @param schema
     * @param startDate
     * @param endDate
     * @param records
     */
    void store(Schema schema, Instant startDate, Instant endDate, List<? extends EnrichedRecord> records);

    /**
     * cleans the store according to the given params
     *
     * @param schema
     * @param startDate
     * @param endDate
     */
    void cleanup(Schema schema, Instant startDate, Instant endDate);
}
