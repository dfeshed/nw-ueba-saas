package fortscale.domain.ad.dao;


import fortscale.domain.ad.AdUserThumbnail;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface AdUserThumbnailService {

    List<AdUserThumbnail> findByObjectGUID(String objectGUID, Pageable pageable);

    List<AdUserThumbnail> save(List<AdUserThumbnail> adUserThumbnails);
}
