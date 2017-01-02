package fortscale.domain.ad.dao;

import com.mongodb.WriteResult;
import fortscale.domain.ad.AdUserThumbnail;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;

import java.util.List;


public class AdUserThumbnailRepositoryImpl implements AdUserThumbnailRepositoryCustom {

    @Autowired
    private MongoTemplate mongoTemplate;

    @Override
    public void upsertBulk(List<AdUserThumbnail> adUserThumbnails) {

        for (AdUserThumbnail adUserThumbnail : adUserThumbnails) {
            final Query query = Query.query(Criteria.where(AdUserThumbnail.FIELD_OBJECT_GUID).is(adUserThumbnail.getObjectGUID()));
            final Update update = Update.update(AdUserThumbnail.FIELD_THUMBNAIL_PHOTO, adUserThumbnail.getThumbnailPhoto());
            final WriteResult upsert = mongoTemplate.upsert(query, update, AdUserThumbnail.COLLECTION_NAME);
        }
//        List<Pair<Query, Update>> updates = new ArrayList<>();
//        for (AdUserThumbnail adUserThumbnail : adUserThumbnails) {
//            final Query query = Query.query(Criteria.where(AdUserThumbnail.FIELD_OBJECT_GUID).is(adUserThumbnail.getObjectGUID()));
//            updates.add(Pair.of(query, Update.update(AdUserThumbnail.FIELD_THUMBNAIL_PHOTO, adUserThumbnail.getThumbnailPhoto())));
//        }
//
//        return mongoTemplate.bulkOps(BulkOperations.BulkMode.UNORDERED, AdUserThumbnail.COLLECTION_NAME)
//                .upsert(updates).execute();

    }
}
