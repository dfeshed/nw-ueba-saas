package org.flume.source.mongo.persistency;

import fortscale.domain.core.AbstractDocument;

import java.time.Instant;
import java.util.List;


public interface SourceMongoRepository {
    List<AbstractDocument> findByDateTimeBetween(String collectionName, Instant startDate,
                                                          Instant endDate, int pageNum, int pageSize,
                                                          String dateTimeField);

}
