package fortscale.collection.jobs.activity;

import fortscale.domain.core.UserActivityLocation;

import java.util.List;

/**
 * @author gils
 * 24/05/2016
 */
public interface UserActivityRawDataRetriever<T> {
    List<UserActivityLocation> retrieve(String dataSource, Long startTime, Long endTime);
}
