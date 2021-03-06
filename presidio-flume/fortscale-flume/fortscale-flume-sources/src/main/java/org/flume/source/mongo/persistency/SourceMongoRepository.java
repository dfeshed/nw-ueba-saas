package org.flume.source.mongo.persistency;

import fortscale.common.general.Schema;
import fortscale.domain.core.AbstractDocument;
import org.flume.source.SourceFetcher;

import java.time.Instant;
import java.util.List;

public interface SourceMongoRepository extends SourceFetcher {

    List<AbstractDocument> findByDateTimeBetween(Schema schema, String collectionName, Instant startDate,
                                                 Instant endDate, int pageNum, int pageSize,
                                                 String dateTimeField);
}
