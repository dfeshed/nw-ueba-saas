package fortscale.domain.core.dao;

import com.google.common.base.Optional;
import fortscale.domain.core.Notification;
import fortscale.domain.core.NotificationAggregate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

import java.util.List;

public interface NotificationsRepositoryCustom {

	List<Notification> findByFsIdExcludeComments(String fsid, boolean includeDissmissed, long before, long after);
	
	List<Notification> findByTsBetweenExcludeComments(Long start, Long end, Sort sort);

	List<NotificationAggregate> findAllAndAggregate(Optional<Integer> daysToFetch, PageRequest request, int maxPages);

	Page<Notification> findByPredicates(List<String> includeFsID, List<String> excludeFsID, boolean includeDissmissed, 
			List<String> includeGenerators, List<String> excludeGenerators, long before, long after, PageRequest request);
}
