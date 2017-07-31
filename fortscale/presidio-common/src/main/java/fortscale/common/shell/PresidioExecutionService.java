package fortscale.common.shell;

import fortscale.common.general.PresidioSchemas;

import java.time.Instant;

/**
 * Created by efratn on 12/06/2017.
 */
public interface PresidioExecutionService {

    public void run(PresidioSchemas presidioSchemas, Instant startDate, Instant endDate, Double fixedDuration) throws Exception;

    public void clean(PresidioSchemas presidioSchemas, Instant startDate, Instant endDate) throws Exception;

    public void cleanAll(PresidioSchemas presidioSchemas) throws Exception;
}
