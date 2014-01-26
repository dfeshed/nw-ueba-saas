package fortscale.domain.fe.dao;

import java.util.List;

import org.joda.time.DateTime;
import org.springframework.data.domain.Pageable;

import fortscale.domain.fe.EventResult;

public interface EventResultRepositoryCustom {
	public List<EventResult> findEventResultsBySqlQueryAndCreatedAt(String sqlQuery, DateTime createdAt, Pageable pageable);
	public List<EventResult> findEventResultsBySqlQueryAndCreatedAtAndGtMinScore(String sqlQuery, DateTime createdAt, Integer minScore, Pageable pageable);
	public void updateLastRetrieved(String sqlQuery, DateTime createdAt);
	public DateTime getLatestCreatedAt();
}
