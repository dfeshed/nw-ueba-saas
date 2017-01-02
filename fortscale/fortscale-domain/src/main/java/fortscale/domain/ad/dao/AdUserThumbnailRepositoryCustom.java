package fortscale.domain.ad.dao;

import fortscale.domain.ad.AdUserThumbnail;

import java.util.List;

public interface AdUserThumbnailRepositoryCustom {


    void upsertBulk(List<AdUserThumbnail> adUserThumbnails);
}
