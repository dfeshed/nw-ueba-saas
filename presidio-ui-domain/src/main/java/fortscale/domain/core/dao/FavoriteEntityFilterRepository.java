package fortscale.domain.core.dao;

import fortscale.domain.core.FavoriteEntityFilter;
import org.springframework.data.mongodb.repository.MongoRepository;

/**
 * Created by alexp on 17/08/2016.
 */
public interface FavoriteEntityFilterRepository
		extends MongoRepository<FavoriteEntityFilter, String>, FavoriteEntityFilterRepositoryCustom {
	long deleteById(String filterId);
}
