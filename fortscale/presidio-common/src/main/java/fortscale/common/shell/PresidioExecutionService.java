package fortscale.common.shell;

import fortscale.common.general.Schema;

import java.time.Instant;

/**
 * Created by efratn on 12/06/2017.
 */
public interface PresidioExecutionService {

    void run(Schema schema, Instant startDate, Instant endDate, Double fixedDuration) throws Exception;

    default void cleanup(Schema schema, Instant startDate, Instant endDate, Double fixedDuration) throws Exception {
    }

    void clean(Schema schema, Instant startDate, Instant endDate) throws Exception;

    void cleanAll(Schema schema) throws Exception;
}
