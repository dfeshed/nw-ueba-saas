package fortscale.domain.ad.dao;

import com.mongodb.BulkWriteResult;
import fortscale.domain.ad.AdUserThumbnail;
import fortscale.domain.core.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.BulkOperations;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.data.util.Pair;

import java.util.ArrayList;
import java.util.List;


public class AdUserThumbnailRepositoryImpl implements AdUserThumbnailRepositoryCustom {

    @Autowired
    private MongoTemplate mongoTemplate;

    @Override
    public BulkWriteResult upsertBulk(List<AdUserThumbnail> adUserThumbnails) {

        List<Pair<Query, Update>> updates = new ArrayList<>();
        for (AdUserThumbnail adUserThumbnail : adUserThumbnails) {
            final Query query = Query.query(Criteria.where(AdUserThumbnail.objectGUIDField).is(adUserThumbnail.getObjectGUID()));
            updates.add(Pair.of(query, Update.update(AdUserThumbnail.thumbnailPhotoField, adUserThumbnail.getThumbnailPhoto())));
        }

        return mongoTemplate.bulkOps(BulkOperations.BulkMode.UNORDERED, User.collectionName)
                .upsert(updates).execute();

    }
}
