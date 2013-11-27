package fortscale.domain.core.dao;

import java.util.List;

import org.springframework.data.domain.PageRequest;

public interface NotificationsRepositoryCustom {

	List<Object> findAllAndAggregate(PageRequest request);

}
