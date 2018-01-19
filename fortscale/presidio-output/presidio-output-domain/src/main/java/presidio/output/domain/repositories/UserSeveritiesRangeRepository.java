package presidio.output.domain.repositories;

import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import presidio.output.domain.records.UserSeveritiesRangeDocument;

public interface UserSeveritiesRangeRepository extends ElasticsearchRepository<UserSeveritiesRangeDocument, String> {

}
