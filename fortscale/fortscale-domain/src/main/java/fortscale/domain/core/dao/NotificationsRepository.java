package fortscale.domain.core.dao;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;

import fortscale.domain.core.Notification;

public interface NotificationsRepository extends MongoRepository<Notification, Long>, NotificationsRepositoryCustom {
	List<Notification> findByFsId(String fsid);
}
