package presidio.monitoring.elastic.repositories;


import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import presidio.monitoring.elastic.records.PresidioMetric;

public interface MetricRepository extends ElasticsearchRepository<PresidioMetric, String> {
}
