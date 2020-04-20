package fortscale.services;

import fortscale.domain.core.Entity;


import java.util.List;
import java.util.Map;



public interface EntityServiceFacade {
	public List<Entity> findBySearchFieldContaining(String prefix, int page, int size);


	public String getUserThumbnail(Entity entity);


	public Boolean isPasswordExpired(Entity entity);

	public Boolean isNoPasswordRequiresValue(Entity entity);

	public Boolean isNormalUserAccountValue(Entity entity);

	public Boolean isPasswordNeverExpiresValue(Entity entity);

	public String getOu(Entity entity);


	public Entity getUserManager(Entity entity, Map<String, Entity> dnToUserMap);

	public List<Entity> getUserDirectReports(Entity entity, Map<String, Entity> dnToUserMap);
}
