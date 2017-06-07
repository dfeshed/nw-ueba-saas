package presidio.ade.domain.store.input;

import org.apache.commons.lang.builder.ToStringBuilder;

import java.time.Duration;
import java.time.Instant;

/**
 * meta data for input records, like who is there data source and what are their time range
 * Created by barak_schuster on 5/18/17.
 */
public class ADEInputRecordsMetaData {
    private final String dataSource;
    private final Instant startInstant;
    private final Instant endInstant;

    public ADEInputRecordsMetaData(String dataSource, Instant startInstant, Instant endInstant) {
        this.dataSource = dataSource;
        this.startInstant = startInstant;
        this.endInstant = endInstant;
    }

    /**
     *
     * @return ToString you know...
     */
    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }

    public String getDataSource() {
        return dataSource;
    }

    public Instant getStartInstant() {
        return startInstant;
    }

    public Instant getEndInstant() {
        return endInstant;
    }
}
