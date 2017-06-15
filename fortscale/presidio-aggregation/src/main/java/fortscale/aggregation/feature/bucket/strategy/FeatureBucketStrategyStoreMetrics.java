package fortscale.aggregation.feature.bucket.strategy;

import fortscale.aggregation.feature.bucket.repository.FeatureBucketMetadataRepository;
import fortscale.utils.monitoring.stats.StatsMetricsGroup;
import fortscale.utils.monitoring.stats.StatsMetricsGroupAttributes;
import fortscale.utils.monitoring.stats.StatsService;
import fortscale.utils.monitoring.stats.annotations.StatsLongMetricParams;
import fortscale.utils.monitoring.stats.annotations.StatsMetricsGroupParams;


@StatsMetricsGroupParams(name = "aggregation.service.feature-bucket-strategy-store")
public class FeatureBucketStrategyStoreMetrics extends StatsMetricsGroup {
    public FeatureBucketStrategyStoreMetrics(StatsService statsService, String storeType) {
        super(statsService, FeatureBucketMetadataRepository.class, new StatsMetricsGroupAttributes() {{
            addTag("storeType", storeType);
        }});
    }

    @StatsLongMetricParams()
    public long saves;
}
