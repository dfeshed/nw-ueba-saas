package com.rsa.asoc.sa.ui.threat.web.socket;

import com.google.common.base.Objects;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * Externalized settings for the {@link IncidentController}.
 *
 * @author Abram Thielke
 * @since 10.6.0.0
 */
@Component
@ConfigurationProperties(prefix = "threats.incident.controller")
public class IncidentControllerSettings {
    /**
     * The number of incidents to send, per message, when streaming the incident to a client.
     */
    private int batchSize = 300;

    public int getBatchSize() {
        return batchSize;
    }

    public void setBatchSize(int batchSize) {
        this.batchSize = batchSize;
    }

    @Override
    public String toString() {
        return Objects.toStringHelper(this)
                .add("batchSize", batchSize)
                .toString();
    }
}
