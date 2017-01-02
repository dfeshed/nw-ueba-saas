package fortscale.domain.ad.dao;

import fortscale.domain.ad.AdUserThumbnail;
import fortscale.utils.logging.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AdUserThumbnailServiceImpl implements AdUserThumbnailService {

    private static final Logger logger = Logger.getLogger(AdUserThumbnailServiceImpl.class);

    @Autowired
    private AdUserThumbnailRepository adUserThumbnailRepository;

    @Override
    public AdUserThumbnail findByObjectGUID(String objectGUID) {
        return adUserThumbnailRepository.findByObjectGUID(objectGUID);
    }

    @Override
    public void upsertBulk(List<AdUserThumbnail> adUserThumbnails) {
//        final BulkWriteResult bulkWriteResult =
//        logger.info("Inserted {} new thumbnails and updated {} existing thumbnails", bulkWriteResult.getUpserts().size(), bulkWriteResult.getModifiedCount());
//        return bulkWriteResult;
        adUserThumbnailRepository.upsertBulk(adUserThumbnails);
    }

}
