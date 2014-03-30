package fortscale.domain.core.dao;

import org.springframework.data.mongodb.repository.MongoRepository;

import fortscale.domain.core.Notification;

public interface NotificationsRepository extends MongoRepository<Notification, String>, NotificationsRepositoryCustom {
}
