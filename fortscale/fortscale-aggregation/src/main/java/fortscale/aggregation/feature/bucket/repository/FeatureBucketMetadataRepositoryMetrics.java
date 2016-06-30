package fortscale.aggregation.feature.bucket.repository;

import fortscale.utils.monitoring.stats.StatsMetricsGroup;
import fortscale.utils.monitoring.stats.StatsMetricsGroupAttributes;
import fortscale.utils.monitoring.stats.StatsService;
import fortscale.utils.monitoring.stats.annotations.StatsDateMetricParams;
import fortscale.utils.monitoring.stats.annotations.StatsLongMetricParams;
import fortscale.utils.monitoring.stats.annotations.StatsMetricsGroupParams;


@StatsMetricsGroupParams(name = "aggregation.service.feature-bucket-metadata-store")
public class FeatureBucketMetadataRepositoryMetrics extends StatsMetricsGroup {
    public FeatureBucketMetadataRepositoryMetrics(StatsService statsService) {
        super(statsService, FeatureBucketMetadataRepository.class, new StatsMetricsGroupAttributes());
    }

    @StatsLongMetricParams()
    public long updates;

    @StatsLongMetricParams()
    public long deletes;

    @StatsDateMetricParams(name="deleteEndTime")
    public long deleteEndEpochtime;

    @StatsDateMetricParams(name="deleteSyncTime")
    public long deleteSyncEpochtime;
}
