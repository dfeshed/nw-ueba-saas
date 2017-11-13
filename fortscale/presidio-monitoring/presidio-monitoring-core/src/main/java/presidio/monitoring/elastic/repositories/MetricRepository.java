package presidio.monitoring.elastic.repositories;


import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;
import presidio.monitoring.records.MetricDocument;

@Repository
public interface MetricRepository extends ElasticsearchRepository<MetricDocument, String> {
}
