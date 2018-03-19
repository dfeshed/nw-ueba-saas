package presidio.output.processor.services;

import java.time.Instant;

/**
 * Created by efratn on 31/07/2017.
 */
public interface OutputExecutionService {
    void run(Instant startDate, Instant endDate) throws Exception;

    void updateAllUsersData() throws Exception;

    void clean(Instant startDate, Instant endDate) throws Exception;

    void applyRetentionPolicy(Instant endDate) throws Exception;

    void cleanAll() throws Exception;

    default int doRun(Instant startDate, Instant endDate) throws Exception {
        try {
            run(startDate, endDate);
        }
        catch (Exception e) {
            return 1;
        }
        return 0;
    }
}
