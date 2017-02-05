package fortscale.domain.ad.dao;

import fortscale.domain.ad.AdUserThumbnail;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface AdUserThumbnailRepository extends MongoRepository<AdUserThumbnail, String> {

	AdUserThumbnail findById(String objectGUID);

}
