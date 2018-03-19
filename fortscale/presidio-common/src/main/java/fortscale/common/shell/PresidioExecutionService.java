package fortscale.common.shell;

import fortscale.common.general.Schema;

import java.time.Instant;

/**
 * Created by efratn on 12/06/2017.
 */
public abstract class PresidioExecutionService {

    public abstract void run(Schema schema, Instant startDate, Instant endDate, Double fixedDuration) throws Exception;

    public abstract void cleanup(Schema schema, Instant startDate, Instant endDate, Double fixedDuration) throws Exception;

    public abstract void applyRetentionPolicy(Schema schema, Instant startDate, Instant endDate) throws Exception;

    public abstract void cleanAll(Schema schema) throws Exception;

    public int doRun(Schema schema, Instant startDate, Instant endDate, Double fixedDuration) throws Exception {
        try {
            run(schema, startDate, endDate, fixedDuration);
        }
        catch (Exception e) {
            return 1;
        }
        return 0;
    }

}
