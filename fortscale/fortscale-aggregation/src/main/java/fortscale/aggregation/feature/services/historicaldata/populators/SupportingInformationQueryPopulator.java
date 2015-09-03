package fortscale.aggregation.feature.services.historicaldata.populators;

import fortscale.aggregation.feature.services.historicaldata.SupportingInformationHistogramData;
import fortscale.domain.core.Evidence;
import fortscale.domain.historical.data.SupportingInformationKey;
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
    protected abstract SupportingInformationKey populate(Evidence evidence, String contextValue, long startTime, long endTime,
                                             Map<SupportingInformationKey, Double> histogramMap,
                                             Map<SupportingInformationKey, Map> additionalInformation);

    /*
     * Basic flow of the populator:
     * 1. Fetch relevant records from Mongo
     * 2. Create the histogram
     * 3. Create the anomaly histogram key
     */
    @Override
    public SupportingInformationHistogramData createSupportingInformationData(Evidence evidence, String contextValue,
                                                                     long evidenceEndTime, int timePeriodInDays) {
        long startTime = TimeUtils.calculateStartingTime(evidenceEndTime, timePeriodInDays);
        Map<SupportingInformationKey, Double> histogramMap = new HashMap<>();
        Map<SupportingInformationKey, Map> additionalInformation = new HashMap<>();
        SupportingInformationKey anomaly = populate(evidence, contextValue, startTime, evidenceEndTime, histogramMap,
                additionalInformation);
        SupportingInformationHistogramData supportingInformationHistogramData;
        if (anomaly != null) {
            supportingInformationHistogramData = new SupportingInformationHistogramData(histogramMap, anomaly);
        } else {
            supportingInformationHistogramData = new SupportingInformationHistogramData(histogramMap);
        }
        supportingInformationHistogramData.setAdditionalInformation(additionalInformation);
        return supportingInformationHistogramData;
    }
}