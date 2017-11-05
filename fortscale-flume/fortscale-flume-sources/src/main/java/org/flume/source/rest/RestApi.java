package org.flume.source.rest;

import fortscale.domain.core.AbstractDocument;
import org.flume.source.SourceFetcher;

import java.time.Instant;
import java.util.List;

public interface RestApi extends SourceFetcher {
    List<AbstractDocument> findByDateTimeBetween(Instant startDate, Instant endDate, int pageNum, int batchSize);
}
