package fortscale.domain.core.dao;

import fortscale.domain.core.FavoriteUserFilter;
import fortscale.domain.rest.EntityFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;

/**
 * Created by alexp on 17/08/2016.
 */
public class FavoriteUserFilterRepositoryImpl implements FavoriteUserFilterRepositoryCustom {

	@Autowired
	private MongoTemplate mongoTemplate;

	@Override public void save(EntityFilter entityFilter, String filterName) {
		FavoriteUserFilter favoriteUserFilter = new FavoriteUserFilter();
		favoriteUserFilter.setFilter(entityFilter);
		favoriteUserFilter.setDateCreated(System.currentTimeMillis());
		favoriteUserFilter.setFilterName(filterName);

		mongoTemplate.insert(favoriteUserFilter);
	}
}
