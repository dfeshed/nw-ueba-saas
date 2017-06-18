package presidio.ade.domain.store;

import org.apache.commons.lang3.builder.ToStringBuilder;

import java.time.Instant;

/**
 * Data cleanup in the store will be preformed by these filtering params.
 * <p>
 * Created by barak_schuster on 5/21/17.
 */
public class AdeDataStoreCleanupParams {
    private final Instant startDate;
    private final Instant endDate;
    private final String dataSource;

    public AdeDataStoreCleanupParams(Instant startDate, Instant endDate, String dataSource) {
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

    /**
     * @return ToString you know...
     */
    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }
}
