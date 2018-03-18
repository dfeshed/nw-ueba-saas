package fortscale.domain.core.dao;

import fortscale.domain.rest.UserFilter;

/**
 * Created by alexp on 17/08/2016.
 */
public interface FavoriteUserFilterRepositoryCustom {
	void save(UserFilter userFilter, String filterName);
}
