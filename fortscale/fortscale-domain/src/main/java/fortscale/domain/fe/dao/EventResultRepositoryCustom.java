package fortscale.domain.fe.dao;

import java.util.List;

import org.springframework.data.domain.Pageable;

import fortscale.domain.fe.EventResult;

public interface EventResultRepositoryCustom {
	public List<EventResult> findEventResultsBySqlQuery(String sqlQuery, Pageable pageable);
	public void updateLastRetrieved(String sqlQuery);
}
