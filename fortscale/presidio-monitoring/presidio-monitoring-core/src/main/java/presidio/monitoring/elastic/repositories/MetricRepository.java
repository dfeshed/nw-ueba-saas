package presidio.monitoring.elastic.repositories;


import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;
import presidio.monitoring.records.MetricDocument;

import java.util.Collection;
import java.util.Date;
import java.util.List;

@Repository
public interface MetricRepository extends ElasticsearchRepository<MetricDocument, String> {

    /**
     * Get metrics from elasticsearch
     * @param names - list of name. One of the names should match to the metic name.
     * @param fromTime - the document timestamp field should greater or equal
     * @param toTime - the document timestamp field should greater or equal
     *
     * @return
     */
    List<MetricDocument> findByNameInAndTimestampGreaterThanEqualAndTimestampLessThan(Collection<String> names, Date fromTime, Date toTime);
    List<MetricDocument> findByNameIn(Collection<String> names);
}
