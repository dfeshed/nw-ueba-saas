package fortscale.services.impl;

import fortscale.domain.core.Entity;
import fortscale.services.*;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service("userServiceFacade")
public class EntityServiceFacadeImpl implements EntityServiceFacade {
	

	private EntityService entityService;

	public EntityServiceFacadeImpl(EntityService entityService) {
		this.entityService = entityService;
	}

	@Override
	public List<Entity> findBySearchFieldContaining(String prefix, int page, int size) {
		return entityService.findBySearchFieldContaining(prefix, page, size);
	}
	


	
	@Override
	public String getUserThumbnail(Entity entity) {
		return entityService.getEntityThumbnail(entity);
	}




	@Override public Boolean isPasswordExpired(Entity entity) {
		return entityService.isPasswordExpired(entity);
	}

	@Override public Boolean isNoPasswordRequiresValue(Entity entity) {
		return entityService.isNoPasswordRequiresValue(entity);
	}

	@Override public Boolean isNormalUserAccountValue(Entity entity) {
		return entityService.isNormalUserAccountValue(entity);
	}

	@Override public Boolean isPasswordNeverExpiresValue(Entity entity) {
		return entityService.isPasswordNeverExpiresValue(entity);
	}

	@Override public String getOu(Entity entity) {
		return entityService.getOu(entity);
	}




	@Override public Entity getUserManager(Entity entity, Map<String, Entity> dnToUserMap) {
		return entityService.getUserManager(entity,dnToUserMap);
	}

	@Override public List<Entity> getUserDirectReports(Entity entity, Map<String, Entity> dnToUserMap) {
		return entityService.getUserDirectReports(entity, dnToUserMap);
	}


}
