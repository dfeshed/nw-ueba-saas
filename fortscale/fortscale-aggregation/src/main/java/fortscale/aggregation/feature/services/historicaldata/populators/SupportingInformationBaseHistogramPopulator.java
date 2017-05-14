package fortscale.aggregation.feature.services.historicaldata.populators;

import fortscale.aggregation.feature.services.historicaldata.SupportingInformationException;
import fortscale.domain.core.Evidence;
import fortscale.domain.historical.data.SupportingInformationKey;

import java.util.Map;

/**
 * Abstract class for supporting information histograms
 *
 * @author gils
 * Date: 17/09/2015
 */
public abstract class SupportingInformationBaseHistogramPopulator implements SupportingInformationDataPopulator {

    protected String contextType;
    protected String dataEntity;
    protected String featureName;

    public SupportingInformationBaseHistogramPopulator(String contextType, String dataEntity, String featureName) {
        this.contextType = contextType;
        this.dataEntity = dataEntity;
        this.featureName = featureName;
    }

    abstract Map<SupportingInformationKey, Double> createSupportingInformationHistogram(String contextValue, long evidenceEndTime, Integer timePeriodInDays,Evidence evidence);

    abstract SupportingInformationKey createAnomalyHistogramKey(Evidence evidence, String featureName);

    protected void validateHistogramDataConsistency(Map<SupportingInformationKey, Double> histogramMap, SupportingInformationKey anomalySupportingInformationKey) {
        if (!histogramMap.containsKey(anomalySupportingInformationKey)) {
            throw new SupportingInformationException("Could not find anomaly histogram key in histogram map. Anomaly key = " + anomalySupportingInformationKey + " # Histogram map = " + histogramMap);
        }
    }

    abstract boolean isAnomalyIndicationRequired(Evidence evidence);

    abstract String getNormalizedContextType(String contextType);

    abstract String getNormalizedFeatureName(String featureName);
}
