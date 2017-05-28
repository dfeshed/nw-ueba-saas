package presidio.ade.domain.store.input;

import org.apache.commons.lang.builder.ToStringBuilder;

import java.time.Duration;
import java.time.Instant;

/**
 * meta data for input records, like who is there data source and what are their time range
 * Created by barak_schuster on 5/18/17.
 */
public class ADEInputRecordsMetaData {
    private String dataSource;
    private Duration dataProcessingDuration;
    private Instant startInstant;
    private Instant endInstant;

    public ADEInputRecordsMetaData(String dataSource, Duration dataProcessingDuration, Instant startInstant, Instant endInstant) {
        this.dataSource = dataSource;
        this.dataProcessingDuration = dataProcessingDuration;
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

    public Duration getDataProcessingDuration() {
        return dataProcessingDuration;
    }

    public Instant getStartInstant() {
        return startInstant;
    }

    public Instant getEndInstant() {
        return endInstant;
    }
}
