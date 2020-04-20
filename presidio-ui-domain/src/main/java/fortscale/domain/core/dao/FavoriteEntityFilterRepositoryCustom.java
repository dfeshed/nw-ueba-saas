package fortscale.domain.core.dao;

import fortscale.domain.rest.EntityFilter;

/**
 * Created by alexp on 17/08/2016.
 */
public interface FavoriteEntityFilterRepositoryCustom {
	void save(EntityFilter entityFilter, String filterName);
}
