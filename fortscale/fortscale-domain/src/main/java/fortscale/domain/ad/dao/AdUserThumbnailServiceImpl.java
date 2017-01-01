package fortscale.domain.ad.dao;

import fortscale.domain.ad.AdUserThumbnail;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.BulkOperations;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AdUserThumbnailServiceImpl implements AdUserThumbnailService {

    @Autowired
    private AdUserThumbnailRepository adUserThumbnailRepository;

    @Override
    public List<AdUserThumbnail> findByObjectGUID(String objectGUID, Pageable pageable) {
        return adUserThumbnailRepository.findByObjectGUID(objectGUID, pageable);
    }

    @Override
    public BulkOperations upsertBulk(List<AdUserThumbnail> adUserThumbnails) {
        return adUserThumbnailRepository.upsertBulk(adUserThumbnails);
    }

}
