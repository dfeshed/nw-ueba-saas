package fortscale.domain.core.dao;

import org.springframework.data.repository.PagingAndSortingRepository;

import fortscale.domain.core.Notification;
import java.lang.String;
import java.util.List;

public interface NotificationsRepository extends PagingAndSortingRepository<Notification, Long> {
	List<Notification> findByFsId(String fsid);
}
