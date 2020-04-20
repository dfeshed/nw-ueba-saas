package presidio.output.sdk.api;

import fortscale.common.general.Schema;
import presidio.output.domain.records.events.EnrichedEvent;

import java.time.Instant;
import java.util.List;

/**
 * Created by efratn on 19/07/2017.
 */
public interface OutputDataServiceSDK {

    /**
     * persist given records into output db
     *
     * @param schema storing is done according to schema
     * @param events data to be stored
     */
    void store(Schema schema, List<? extends EnrichedEvent> events) throws Exception;

    void clean(Schema schema, Instant startDate, Instant endDate);
}
