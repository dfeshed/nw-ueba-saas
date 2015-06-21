package fortscale.domain.core.dao;

import fortscale.domain.core.Alert;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;

import java.util.List;

/**
 * Created by rans on 21/06/15.
 */
public class AlertsRepositoryImpl implements AlertsRepositoryCustom {
    @Autowired
    private MongoTemplate mongoTemplate;
    @Override
    public List<Alert> findAll(PageRequest request, int maxPages) {
        Query query = new Query( ).with( request.getSort() );
        query.fields().exclude("comments");
        return mongoTemplate.find(query, Alert.class);
    }
}
