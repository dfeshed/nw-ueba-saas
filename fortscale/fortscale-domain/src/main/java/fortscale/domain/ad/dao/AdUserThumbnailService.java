package fortscale.domain.ad.dao;


import com.mongodb.BulkWriteResult;
import fortscale.domain.ad.AdUserThumbnail;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface AdUserThumbnailService {

    List<AdUserThumbnail> findByObjectGUID(String objectGUID, Pageable pageable);

    BulkWriteResult upsertBulk(List<AdUserThumbnail> adUserThumbnails);
}
