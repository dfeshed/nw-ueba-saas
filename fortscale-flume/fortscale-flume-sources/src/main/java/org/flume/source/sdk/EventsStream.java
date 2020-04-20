package org.flume.source.sdk;

import fortscale.common.general.Schema;
import fortscale.domain.core.AbstractDocument;
import org.flume.source.SourceFetcher;

import java.time.Instant;
import java.util.Map;

public interface EventsStream extends SourceFetcher {

    void startStreaming(Schema schema, Instant startDate, Instant endDate, Map<String, String> config);

    boolean hasNext();

    AbstractDocument next();

    void stopStreaming();

}
