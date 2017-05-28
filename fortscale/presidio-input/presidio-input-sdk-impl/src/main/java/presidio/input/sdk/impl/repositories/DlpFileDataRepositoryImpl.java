package presidio.input.sdk.impl.repositories;

import fortscale.common.general.CommonStrings;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import presidio.sdk.api.domain.DlpFileDataDocument;

import java.util.List;

public class DlpFileDataRepositoryImpl implements DlpFileDataRepositoryCustom {

    private final MongoTemplate mongoTemplate;

    public DlpFileDataRepositoryImpl(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    @Override
    public List<DlpFileDataDocument> find(long startTime, long endTime) {
        Criteria startTimeCriteria = Criteria.where(CommonStrings.DATE_TIME_UNIX_FIELD_NAME).gte(startTime);
        Criteria endTimeCriteria = Criteria.where(CommonStrings.DATE_TIME_UNIX_FIELD_NAME).lte(endTime);
        final Query query = new Query(startTimeCriteria).addCriteria(endTimeCriteria);
        return mongoTemplate.find(query, DlpFileDataDocument.class);
    }
}
