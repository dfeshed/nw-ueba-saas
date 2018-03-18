package fortscale.domain.core.dao;


import fortscale.domain.core.alert.analystfeedback.AnalystFeedback;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Set;

@Repository
public interface AlertCommentsRepository extends MongoRepository<AnalystFeedback, String>{

    List<AnalystFeedback> findByAlertId(String alertId);
    List<AnalystFeedback> findByAlertIdIn(Set<String> alertIds);

}