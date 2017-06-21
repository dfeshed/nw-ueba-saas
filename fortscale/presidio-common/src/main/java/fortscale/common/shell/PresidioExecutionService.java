package fortscale.common.shell;

import fortscale.common.general.DataSource;

import java.time.Instant;

/**
 * Created by efratn on 12/06/2017.
 */
public interface PresidioExecutionService {

    public void run(DataSource dataSource, Instant startDate, Instant endDate, Long fixedDuration) throws Exception;

    public void clean(DataSource dataSource, Instant startDate, Instant endDate) throws Exception;

    public void cleanAll(DataSource dataSource) throws Exception;
}
