package fortscale.collection.jobs.activity;

import java.util.List;

/**
 * @author gils
 * 24/05/2016
 */
public interface UserActivityRawDataRetriever<T> {
    List<T> retrieve(String dataSource, Long startTime, Long endTime);
}
