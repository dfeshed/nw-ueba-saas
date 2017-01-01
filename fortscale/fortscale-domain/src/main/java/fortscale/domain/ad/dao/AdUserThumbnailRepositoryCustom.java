package fortscale.domain.ad.dao;

import fortscale.domain.ad.AdUserThumbnail;
import org.springframework.data.mongodb.core.BulkOperations;

import java.util.List;

public interface AdUserThumbnailRepositoryCustom {


    BulkOperations upsertBulk(List<AdUserThumbnail> adUserThumbnails);
}
