package fortscale.common.shell;

import fortscale.common.general.Schema;

import java.time.Instant;

/**
 * Created by efratn on 12/06/2017.
 */
public interface PresidioExecutionService {

    void run(Schema schema, Instant startDate, Instant endDate, Double fixedDuration) throws Exception;

    void cleanup(Schema schema, Instant startDate, Instant endDate, Double fixedDuration) throws Exception;

    void applyRetentionPolicy(Schema schema, Instant startDate, Instant endDate) throws Exception;

    void cleanAll(Schema schema) throws Exception;

    default int doRun(Schema schema, Instant startDate, Instant endDate, Double fixedDuration) throws Exception {
        try {
            run(schema, startDate, endDate, fixedDuration);
        }
        catch (Exception e) {
            return 1;
        }
        return 0;
    }

    default int doCleanup(Schema schema, Instant startDate, Instant endDate, Double fixedDuration) throws Exception {
        try {
            cleanup(schema, startDate, endDate, fixedDuration);
        }
        catch (Exception e) {
            return 1;
        }
        return 0;
    }

    default int doApplyRetentionPolicy(Schema schema, Instant startDate, Instant endDate) throws Exception {
        try {
            applyRetentionPolicy(schema, startDate, endDate);
        }
        catch (Exception e) {
            return 1;
        }
        return 0;
    }

    default int doCleanAll(Schema schema) throws Exception {
        try {
            cleanAll(schema);
        }
        catch (Exception e) {
            return 1;
        }
        return 0;
    }



}
