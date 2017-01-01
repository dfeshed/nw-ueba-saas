package fortscale.domain.ad.dao;


import fortscale.domain.ad.AdUserThumbnail;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.BulkOperations;

import java.util.List;

public interface AdUserThumbnailService {

    List<AdUserThumbnail> findByObjectGUID(String objectGUID, Pageable pageable);

    BulkOperations upsertBulk(List<AdUserThumbnail> adUserThumbnails);
}
