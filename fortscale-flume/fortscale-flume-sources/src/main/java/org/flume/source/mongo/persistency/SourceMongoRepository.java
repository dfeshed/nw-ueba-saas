package org.flume.source.mongo.persistency;


import fortscale.domain.core.AbstractAuditableDocument;

import java.time.Instant;
import java.util.List;


public interface SourceMongoRepository {

    List<AbstractAuditableDocument> findByDateTimeBetween(String collectionName, Instant startDate, Instant endDate, int pageNum, int pageSize);

}
