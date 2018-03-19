package presidio.output.processor.services;

import java.time.Instant;

/**
 * Created by efratn on 31/07/2017.
 */
public abstract class OutputExecutionService {
    public abstract void run(Instant startDate, Instant endDate) throws Exception;

    public abstract void updateAllUsersData() throws Exception;

    public abstract void clean(Instant startDate, Instant endDate) throws Exception;

    public abstract void applyRetentionPolicy(Instant endDate) throws Exception;

    public abstract void cleanAll() throws Exception;

    public int doRun(Instant startDate, Instant endDate) throws Exception {
        try {
            run(startDate, endDate);
        }
        catch (Exception e) {
            return 1;
        }
        return 0;
    }
}
