package fortscale.domain.ad.dao;

import com.mongodb.BulkWriteResult;
import fortscale.domain.ad.AdUserThumbnail;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.BulkOperations;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.data.util.Pair;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;


public class AdUserThumbnailRepositoryImpl implements AdUserThumbnailRepositoryCustom {

    @Autowired
    private MongoTemplate mongoTemplate;

    @Override
    public BulkWriteResult upsertBulk(List<AdUserThumbnail> adUserThumbnails) {

        List<Pair<Query, Update>> updates = new ArrayList<>();
        for (AdUserThumbnail adUserThumbnail : adUserThumbnails) {
            final Query matchObjectGuid = Query.query(Criteria.where(AdUserThumbnail.FIELD_OBJECT_GUID).is(adUserThumbnail.getObjectGUID()));
            final Update updateThumbnailPhoto = Update.update(AdUserThumbnail.FIELD_THUMBNAIL_PHOTO, adUserThumbnail.getThumbnailPhoto());
            final Update updateModifiedAt = Update.update(AdUserThumbnail.FIELD_MODIFIED_AT, Instant.now());
            updates.add(Pair.of(matchObjectGuid, updateThumbnailPhoto));
            updates.add(Pair.of(matchObjectGuid, updateModifiedAt));

//            final Query createdAtExists = Query.query(Criteria.where(AdUserThumbnail.FIELD_CREATED_AT).exists(false));
//            final Update updateCreatedAt = Update.update(AdUserThumbnail.FIELD_CREATED_AT, Instant.now());
//            updates.add(Pair.of(createdAtExists, updateCreatedAt));

        }

        return mongoTemplate.bulkOps(BulkOperations.BulkMode.ORDERED, AdUserThumbnail.COLLECTION_NAME)
                .upsert(updates).execute();

    }
}
