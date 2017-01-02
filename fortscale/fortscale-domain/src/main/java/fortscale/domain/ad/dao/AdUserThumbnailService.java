package fortscale.domain.ad.dao;


import com.mongodb.BulkWriteResult;
import fortscale.domain.ad.AdUserThumbnail;

import java.util.List;

public interface AdUserThumbnailService {

    AdUserThumbnail findByObjectGUID(String objectGUID);

    BulkWriteResult upsertBulk(List<AdUserThumbnail> adUserThumbnails);
}
