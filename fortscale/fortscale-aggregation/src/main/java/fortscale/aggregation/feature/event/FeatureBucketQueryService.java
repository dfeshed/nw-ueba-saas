package fortscale.aggregation.feature.event;

import fortscale.aggregation.feature.bucket.FeatureBucket;

import java.util.List;

/**
 * Service to provide basic query functionality of aggregated events
 *
 * @author Amir Keren
 * Date: 15/11/2015
 */
public interface FeatureBucketQueryService {
    List<FeatureBucket> getFeatureBucketsByContextAndTimeRange(String featureName, String contextType, String ContextName, Long startTime, Long endTime);
}
