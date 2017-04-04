package fortscale.collection.services;

import fortscale.collection.metrics.ETLCommonJobMetrics;
import fortscale.collection.morphlines.metrics.MorphlineMetrics;

public interface CollectionStatsMetricsService {

	MorphlineMetrics getMorphlineMetrics(String dataSource);

	ETLCommonJobMetrics getETLCommonJobMetrics(String dataSource);

}
