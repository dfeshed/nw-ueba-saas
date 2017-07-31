package presidio.input.core.services.data;

import fortscale.common.general.PresidioSchemas;
import presidio.ade.domain.record.enriched.EnrichedRecord;

import java.time.Instant;
import java.util.List;

public interface AdeDataService {

    /**
     * stores the given records
     *
     * @param presidioSchemas
     * @param startDate
     * @param endDate
     * @param records
     */
    void store(PresidioSchemas presidioSchemas, Instant startDate, Instant endDate, List<? extends EnrichedRecord> records);

    /**
     * cleans the store according to the given params
     *
     * @param presidioSchemas
     * @param startDate
     * @param endDate
     */
    void cleanup(PresidioSchemas presidioSchemas, Instant startDate, Instant endDate);
}
