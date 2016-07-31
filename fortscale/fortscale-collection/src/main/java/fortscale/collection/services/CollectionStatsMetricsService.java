package fortscale.collection.services;

import fortscale.collection.metrics.ETLCommonJobMetircs;
import fortscale.collection.morphlines.metrics.MorphlineMetrics;

/**
 * Created by idanp on 6/27/2016.
 */
public interface CollectionStatsMetricsService {

	public MorphlineMetrics getMorphlineMetrics(String dataSource);

	public ETLCommonJobMetircs getETLCommonJobMetircs(String dataSource);

}
