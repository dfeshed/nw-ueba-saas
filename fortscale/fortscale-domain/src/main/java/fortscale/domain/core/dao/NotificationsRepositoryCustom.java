package fortscale.domain.core.dao;

import java.util.Date;
import java.util.List;
import java.util.Set;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

import fortscale.domain.core.Notification;
import fortscale.domain.core.NotificationAggregate;

public interface NotificationsRepositoryCustom {

	List<Notification> findByFsIdExcludeComments(String fsid);
	
	List<Notification> findByTsGreaterThanExcludeComments(long ts, Sort sort);
	
	List<NotificationAggregate> findAllAndAggregate(PageRequest request);

	Page<Notification> findByPredicates(Set<String> includeFsID, Set<String> excludeFsID, boolean includeDissmissed, 
			Set<String> includeGenerators, Set<String> excludeGenerators, Date before, Date after, PageRequest request);
}
