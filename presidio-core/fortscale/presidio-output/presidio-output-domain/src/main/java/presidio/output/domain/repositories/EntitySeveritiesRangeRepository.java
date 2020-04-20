package presidio.output.domain.repositories;

import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import presidio.output.domain.records.EntitySeveritiesRangeDocument;

public interface EntitySeveritiesRangeRepository extends ElasticsearchRepository<EntitySeveritiesRangeDocument, String> {

}
