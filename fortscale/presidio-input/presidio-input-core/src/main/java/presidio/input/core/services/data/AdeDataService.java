package presidio.input.core.services.data;

import fortscale.common.general.DataSource;
import presidio.ade.domain.record.enriched.EnrichedRecord;

import java.time.Instant;
import java.util.List;

public interface AdeDataService {

    /**
     * stores the given records
     *
     * @param dataSource
     * @param startDate
     * @param endDate
     * @param records
     */
    void store(DataSource dataSource, Instant startDate, Instant endDate, List<? extends EnrichedRecord> records);

    /**
     * cleans the store according to the given params
     *
     * @param dataSource
     * @param startDate
     * @param endDate
     */
    void cleanup(DataSource dataSource, Instant startDate, Instant endDate);
}
