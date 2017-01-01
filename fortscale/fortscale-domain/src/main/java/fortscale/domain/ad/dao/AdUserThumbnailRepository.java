package fortscale.domain.ad.dao;

import fortscale.domain.ad.AdUserThumbnail;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface AdUserThumbnailRepository extends MongoRepository<AdUserThumbnail, String> {

	List<AdUserThumbnail> findById(String objectGUID, Pageable pageable);

	List<AdUserThumbnail> save(List<AdUserThumbnail> adUserThumbnails);
}
