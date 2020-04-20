package presidio.output.processor.services.alert.indicator.enricher;

import fortscale.aggregation.feature.bucket.FeatureBucket;
import fortscale.common.feature.FeatureValue;
import fortscale.utils.time.TimeRange;
import org.apache.commons.lang3.Validate;
import org.springframework.context.ApplicationContext;
import presidio.ade.sdk.common.AdeManagerSdk;
import presidio.output.domain.records.alerts.Indicator;
import presidio.output.processor.config.IndicatorConfig;

public abstract class FeatureBucketIndicatorEnricher implements IndicatorEnricher.Implementable {
    private static final int DEFAULT_PAGE_SIZE = 1000;

    private AdeManagerSdk adeManagerSdk;
    private String featureBucketConfName;
    private String aggregatedFeatureConfName;
    private int pageSize;

    public FeatureBucketIndicatorEnricher(String featureBucketConfName, String aggregatedFeatureConfName, Integer pageSize) {
        this.featureBucketConfName = Validate.notBlank(featureBucketConfName, "featureBucketConfName cannot be blank.");
        this.aggregatedFeatureConfName = Validate.notBlank(aggregatedFeatureConfName, "aggregatedFeatureConfName cannot be blank.");
        this.pageSize = pageSize == null ? DEFAULT_PAGE_SIZE : pageSize;
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) {
        adeManagerSdk = applicationContext.getBean(AdeManagerSdk.class);
    }

    @Override
    public void enrichIndicator(IndicatorConfig indicatorConfig, Indicator indicator) {
        FeatureBucket featureBucket = adeManagerSdk.createFeatureBucketFromEnrichedRecords(
                featureBucketConfName,
                new TimeRange(indicator.getStartDate(), indicator.getEndDate()),
                indicator.getContexts(),
                pageSize);
        FeatureValue featureValue = featureBucket.getAggregatedFeatures().get(aggregatedFeatureConfName).getValue();
        enrichIndicator(indicatorConfig, indicator, featureValue);
    }

    abstract void enrichIndicator(IndicatorConfig indicatorConfig, Indicator indicator, FeatureValue featureValue);
}
