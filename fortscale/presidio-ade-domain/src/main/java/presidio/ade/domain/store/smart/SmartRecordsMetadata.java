package presidio.ade.domain.store.smart;

import org.apache.commons.lang.builder.ToStringBuilder;

import java.time.Instant;

/**
 * metadata for smart records
 */
public class SmartRecordsMetadata {
    private final String configurationName;
    private final Instant startInstant;
    private final Instant endInstant;

    public SmartRecordsMetadata(String configurationName, Instant startInstant, Instant endInstant) {
        this.configurationName = configurationName;
        this.startInstant = startInstant;
        this.endInstant = endInstant;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }

    public String getConfigurationName() {
        return configurationName;
    }

    public Instant getStartInstant() {
        return startInstant;
    }

    public Instant getEndInstant() {
        return endInstant;
    }
}
