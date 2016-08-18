package fortscale.domain.core.dao;

import fortscale.domain.core.FavoriteUserFilter;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

/**
 * Created by alexp on 17/08/2016.
 */
public interface FavoriteUserFilterRepository
		extends MongoRepository<FavoriteUserFilter, String>, FavoriteUserFilterRepositoryCustom {
	long deleteByFilterName(String filterName);
}
