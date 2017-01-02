package fortscale.domain.ad.dao;


import fortscale.domain.ad.AdUserThumbnail;

import java.util.List;

public interface AdUserThumbnailService {

    AdUserThumbnail findByObjectGUID(String objectGUID);

    void upsertBulk(List<AdUserThumbnail> adUserThumbnails);
}
