package presidio.monitoring.elastic.allindexrepo;


import org.springframework.stereotype.Repository;
import presidio.monitoring.records.MetricDocument;

import java.util.Collection;
import java.util.List;

@Repository
public interface MetricsAllIndexesRepository extends org.springframework.data.repository.Repository<MetricDocument, String> {

    /**
     * Get metrics from elasticsearch
     * @param names - list of name. One of the names should match to the metic name.
     * @param fromTime - the document timestamp field should greater or equal
     * @param toTime - the document timestamp field should greater or equal
     *
     * @return
     */
    List<MetricDocument> findByNameInAndLogicTimeGreaterThanEqualAndLogicTimeLessThan(Collection<String> names, long fromTime, long toTime);
}
