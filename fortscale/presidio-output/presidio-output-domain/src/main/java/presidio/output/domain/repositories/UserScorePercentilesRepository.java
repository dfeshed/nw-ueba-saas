package presidio.output.domain.repositories;

import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import presidio.output.domain.records.UserScorePercentilesDocument;

/**
 * Created by Efrat Noam on 12/5/17.
 */
public interface UserScorePercentilesRepository extends ElasticsearchRepository<UserScorePercentilesDocument, String> {

}
