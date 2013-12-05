package fortscale.domain.core.dao;

import fortscale.domain.core.NotificationResource;

public interface NotificationResourcesRepositoryCustom {
	NotificationResource findByMsg_name(String msg_name);
}