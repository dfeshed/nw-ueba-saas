package presidio.monitoring.repository;


import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;
import presidio.monitoring.elastic.services.MetricDocumentStaticIndexForTests;


@Repository
public interface MetricRepositoryStaticIndexForTests extends ElasticsearchRepository<MetricDocumentStaticIndexForTests, String> {
}
