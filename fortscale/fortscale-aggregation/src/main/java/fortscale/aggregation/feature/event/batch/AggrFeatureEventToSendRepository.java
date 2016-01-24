package fortscale.aggregation.feature.event.batch;

import org.springframework.data.mongodb.repository.MongoRepository;

/**
 * Created by YaronDL on 12/31/2015.
 */
public interface AggrFeatureEventToSendRepository extends MongoRepository<AggrFeatureEventToSend, String>, AggrFeatureEventToSendRepositoryCustom {
}
