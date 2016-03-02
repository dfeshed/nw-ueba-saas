package fortscale.domain.core.dao;

import fortscale.domain.core.AnalyticEvent;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface AnalyticEventsRepository extends MongoRepository<AnalyticEvent,String>, AnalyticEventsRepositoryCustom {
}
