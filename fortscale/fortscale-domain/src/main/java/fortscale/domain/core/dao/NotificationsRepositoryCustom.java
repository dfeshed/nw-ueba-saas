package fortscale.domain.core.dao;

import java.util.List;

import org.springframework.data.domain.PageRequest;

import fortscale.domain.core.NotificationAggregate;

public interface NotificationsRepositoryCustom {

	List<NotificationAggregate> findAllAndAggregate(PageRequest request);

}
