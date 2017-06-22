package presidio.input.sdk.impl.repositories;

import com.mongodb.WriteResult;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;
import presidio.sdk.api.domain.DlpFileDataDocument;

import java.time.Instant;
import java.util.List;

@Repository
public class DlpFileDataRepositoryImpl implements DlpFileDataRepositoryCustom {

    private final MongoTemplate mongoTemplate;

    public DlpFileDataRepositoryImpl(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    @Override
    public List<DlpFileDataDocument> find(Instant startTime, Instant endTime) {
        Criteria timeCriteria = Criteria.where(DlpFileDataDocument.DATE_TIME_FIELD_NAME).gte(startTime).lt(endTime);
        final Query query = new Query(timeCriteria);//todo: need to do this 2 lines else where , will be done when new data sources will be add and this class will be refactored
        return mongoTemplate.find(query, DlpFileDataDocument.class);
    }

    @Override
    public int clean(Instant startTime, Instant endTime) {
        Criteria timeCriteria = Criteria.where(DlpFileDataDocument.DATE_TIME_FIELD_NAME).gte(startTime).lt(endTime);
        final Query query = new Query(timeCriteria);//todo:same as the the todo above
        WriteResult removeResult = mongoTemplate.remove(query, DlpFileDataDocument.class, DlpFileDataDocument.COLLECTION_NAME);
        return removeResult.getN();
    }
}
