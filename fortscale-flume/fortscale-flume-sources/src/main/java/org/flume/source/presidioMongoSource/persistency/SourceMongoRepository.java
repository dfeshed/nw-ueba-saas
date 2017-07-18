package org.flume.source.presidioMongoSource.persistency;



import org.flume.domain.AbstractDocument;

import java.time.Instant;
import java.util.List;


public interface SourceMongoRepository {

    List<AbstractDocument> findByDateTimeBetween(String collectionName, Instant startDate, Instant endDate, int pageNum, int pageSize);

}
