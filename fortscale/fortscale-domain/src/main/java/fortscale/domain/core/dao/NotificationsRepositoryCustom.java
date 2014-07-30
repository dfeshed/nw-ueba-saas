package fortscale.domain.core.dao;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

import com.google.common.base.Optional;

import fortscale.domain.core.Notification;
import fortscale.domain.core.NotificationAggregate;

public interface NotificationsRepositoryCustom {

	List<Notification> findByFsIdExcludeComments(String fsid, boolean includeDissmissed, Optional<Integer> daysToFetch);
	
	List<Notification> findByTsGreaterThanExcludeComments(long ts, Sort sort);
	
	List<NotificationAggregate> findAllAndAggregate(Optional<Integer> daysToFetch, PageRequest request, int maxPages);

	Page<Notification> findByPredicates(List<String> includeFsID, List<String> excludeFsID, boolean includeDissmissed, 
			List<String> includeGenerators, List<String> excludeGenerators, long before, long after, PageRequest request);
}
