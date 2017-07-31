package fortscale.common.shell;

import fortscale.common.general.Schema;

import java.time.Instant;

/**
 * Created by efratn on 12/06/2017.
 */
public interface PresidioExecutionService {

    public void run(Schema schema, Instant startDate, Instant endDate, Double fixedDuration) throws Exception;

    public void clean(Schema schema, Instant startDate, Instant endDate) throws Exception;

    public void cleanAll(Schema schema) throws Exception;
}
