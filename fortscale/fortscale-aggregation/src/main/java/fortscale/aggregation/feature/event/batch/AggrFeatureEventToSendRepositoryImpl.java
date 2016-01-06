package fortscale.aggregation.feature.event.batch;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;

import java.util.List;

import static org.springframework.data.mongodb.core.query.Criteria.where;

/**
 * Created by YaronDL on 12/31/2015.
 */
public class AggrFeatureEventToSendRepositoryImpl implements AggrFeatureEventToSendRepositoryCustom{

    @Autowired
    private MongoTemplate mongoTemplate;



    @Override
    public List<AggrFeatureEventToSend> findByEndTimeBetween(Long lowerTimeSec, Long upperTimeSec, Pageable pageable) {
        Query query = new Query(where(AggrFeatureEventToSend.END_TIME_FIELD).gt(lowerTimeSec).lte(upperTimeSec));
        if(pageable != null){
            query.with(pageable);
        }
        return mongoTemplate.find(query, AggrFeatureEventToSend.class);
    }

    @Override
    public void deleteByEndTimeBetween(Long lowerTimeSec, Long upperTimeSec){
        Query query = new Query(where(AggrFeatureEventToSend.END_TIME_FIELD).gt(lowerTimeSec).lte(upperTimeSec));
        mongoTemplate.remove(query, AggrFeatureEventToSend.class);
    }
}
