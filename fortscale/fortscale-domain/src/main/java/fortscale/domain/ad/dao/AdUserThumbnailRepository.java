package fortscale.domain.ad.dao;

import fortscale.domain.ad.AdUserThumbnail;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface AdUserThumbnailRepository extends MongoRepository<AdUserThumbnail, String> {

	AdUserThumbnail findById(String objectGUID);

	List<AdUserThumbnail> save(List<AdUserThumbnail> adUserThumbnails);
}
