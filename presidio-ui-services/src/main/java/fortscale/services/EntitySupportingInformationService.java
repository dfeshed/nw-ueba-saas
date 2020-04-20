package fortscale.services;

import fortscale.domain.core.Entity;
import fortscale.domain.core.UserSupportingInformation;

/**
 * Created by galiar on 20/08/2015.
 */
public interface EntitySupportingInformationService {

	public UserSupportingInformation createEntitySupportingInformation(Entity entity, EntityService entityService);
}
