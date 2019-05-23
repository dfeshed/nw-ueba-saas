package fortscale.domain.core.dao;

import fortscale.domain.core.FavoriteEntityFilter;
import fortscale.domain.rest.EntityFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;

/**
 * Created by alexp on 17/08/2016.
 */
public class FavoriteEntityFilterRepositoryImpl implements FavoriteEntityFilterRepositoryCustom {

	@Autowired
	private MongoTemplate mongoTemplate;

	@Override public void save(EntityFilter entityFilter, String filterName) {
		FavoriteEntityFilter favoriteUserFilter = new FavoriteEntityFilter();
		favoriteUserFilter.setFilter(entityFilter);
		favoriteUserFilter.setDateCreated(System.currentTimeMillis());
		favoriteUserFilter.setFilterName(filterName);

		mongoTemplate.insert(favoriteUserFilter);
	}
}
