package fortscale.domain.ad.dao;

import fortscale.domain.ad.AdUserThumbnail;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface AdUserThumbnailRepository extends MongoRepository<AdUserThumbnail, String>, AdUserThumbnailRepositoryCustom {

	List<AdUserThumbnail> findByObjectGUID(String objectGUID, Pageable pageable);
}
