package presidio.ade.domain.store.input;

import java.time.Instant;

/**
 * cleanup would be preformed by those filtering params
 * Created by barak_schuster on 5/21/17.
 */
public class ADEInputCleanupParams {
    private Instant startDate;
    private Instant endDate;
    private String dataSource;

    public ADEInputCleanupParams(Instant startDate, Instant endDate, String dataSource) {
        this.startDate = startDate;
        this.endDate = endDate;
        this.dataSource = dataSource;
    }

    public Instant getStartDate() {
        return startDate;
    }

    public Instant getEndDate() {
        return endDate;
    }

    public String getDataSource() {
        return dataSource;
    }
}
