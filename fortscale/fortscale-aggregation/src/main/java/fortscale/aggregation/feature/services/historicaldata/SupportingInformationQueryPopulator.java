package fortscale.aggregation.feature.services.historicaldata;

import fortscale.domain.core.Evidence;
import fortscale.domain.core.SupportingInformationData;
import fortscale.domain.histogram.HistogramKey;
import fortscale.utils.time.TimeUtils;

import java.util.HashMap;
import java.util.Map;

/**
 * Abstract class for Mongo queries populator
 *
 * @author Amir Keren
 * Date: 18/08/2015
 */
public abstract class SupportingInformationQueryPopulator implements SupportingInformationDataPopulator {

    //this method will vary according to which Mongo service will be used to fetch the data from
    protected abstract HistogramKey populate(String contextValue, long startTime, long endTime,
                                             Map<HistogramKey, Double> histogramMap,
                                             Map<HistogramKey, Map> additionalInformation);

    /*
     * Basic flow of the populator:
     * 1. Fetch relevant records from Mongo
     * 2. Create the histogram
     * 3. Create the anomaly histogram key
     */
    @Override
    public SupportingInformationData createSupportingInformationData(Evidence evidence, String contextValue, long evidenceEndTime,
                                                                     int timePeriodInDays, boolean shouldExtractAnomalyValue) {
        long startTime = TimeUtils.calculateStartingTime(evidenceEndTime, timePeriodInDays);
        Map<HistogramKey, Double> histogramMap = new HashMap<>();
        Map<HistogramKey, Map> additionalInformation = new HashMap<>();
        HistogramKey anomaly = populate(contextValue, startTime, evidenceEndTime, histogramMap, additionalInformation);
        SupportingInformationData supportingInformationData;
        if (anomaly != null) {
            supportingInformationData = new SupportingInformationData(histogramMap, anomaly);
        } else {
            supportingInformationData = new SupportingInformationData(histogramMap);
        }
        supportingInformationData.setAdditionalInformation(additionalInformation);
        return supportingInformationData;
    }
}