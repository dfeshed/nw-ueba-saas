package presidio.input.sdk.impl.repositories;

import fortscale.common.general.CommonStrings;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;
import presidio.sdk.api.domain.DlpFileDataDocument;

import java.util.List;

@Repository
public class DlpFileDataRepositoryImpl implements DlpFileDataRepositoryCustom {

    private final MongoTemplate mongoTemplate;

    public DlpFileDataRepositoryImpl(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    @Override
    public List<DlpFileDataDocument> find(long startTime, long endTime) {
        Criteria timeCriteria = Criteria.where(CommonStrings.DATE_TIME_UNIX_FIELD_NAME).gte(startTime).lte(endTime);
        final Query query = new Query(timeCriteria);
        return mongoTemplate.find(query, DlpFileDataDocument.class);
    }

    @Override
    public int clean(long startTime, long endTime) {
        Criteria timeCriteria = Criteria.where(CommonStrings.DATE_TIME_UNIX_FIELD_NAME).gte(startTime).lte(endTime);
        final Query query = new Query(timeCriteria);
        return mongoTemplate.findAllAndRemove(query, DlpFileDataDocument.class, CommonStrings.COLLECTION_NAME).size();
    }
}
