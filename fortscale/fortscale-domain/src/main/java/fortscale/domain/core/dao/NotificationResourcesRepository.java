package fortscale.domain.core.dao;

import org.springframework.data.mongodb.repository.MongoRepository;

import fortscale.domain.core.NotificationResource;

public interface NotificationResourcesRepository extends
		MongoRepository<NotificationResource, Long>, NotificationResourcesRepositoryCustom {
}
