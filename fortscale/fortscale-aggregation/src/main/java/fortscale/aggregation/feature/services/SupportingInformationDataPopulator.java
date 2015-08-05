package fortscale.aggregation.feature.services;

import fortscale.aggregation.feature.bucket.FeatureBucket;
import fortscale.domain.core.HistogramKey;
import fortscale.domain.core.SupportingInformationData;

import java.util.List;
import java.util.Map;

/**
 * @author gils
 * Date: 05/08/2015
 */
public interface SupportingInformationDataPopulator {
    SupportingInformationData createSupportingInformationData(String contextValue, long evidenceEndTime, int timePeriodInDays, String anomalyValue);
}
