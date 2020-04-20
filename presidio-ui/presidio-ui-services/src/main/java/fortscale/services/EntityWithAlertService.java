package fortscale.services;

import fortscale.domain.core.Entity;
import fortscale.domain.rest.EntityRestFilter;
import org.springframework.data.domain.PageRequest;

import java.util.List;

public interface EntityWithAlertService {

	List<Entity> findEntitiesByFilter(EntityRestFilter entityRestFilter, PageRequest pageRequest, List<String> fieldsRequired, boolean fetchEntitiesAlerts);

	int countEntitiesByFilter(EntityRestFilter entityRestFilter);



    int updateTags(EntityRestFilter entityRestFilter, Boolean addTag, List<String> tagNames) throws Exception;

	int followEntitiesByFilter(EntityRestFilter entityRestFilter, Boolean watch);


}