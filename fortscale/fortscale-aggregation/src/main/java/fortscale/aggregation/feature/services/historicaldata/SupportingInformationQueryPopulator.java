package fortscale.aggregation.feature.services.historicaldata;

import fortscale.domain.core.SupportingInformationData;
import fortscale.domain.events.VpnSession;
import fortscale.domain.histogram.HistogramKey;
import fortscale.domain.histogram.HistogramSingleKey;
import fortscale.services.event.VpnService;
import fortscale.utils.time.TimeUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Abstract class for Mongo queries populator
 *
 * @author Amir Keren
 * Date: 18/08/2015
 */
public abstract class SupportingInformationQueryPopulator implements SupportingInformationDataPopulator {

    //this method will vary according to which mongo service will be used to fetch the data from
    protected abstract HistogramKey populate(String contextValue, long evidenceEndTime, long from,
                                             Map<HistogramKey, Double> histogramMap,
                                             Map<HistogramKey, Map> additionalInformation, String anomalyValue);

    @Override
    public SupportingInformationData createSupportingInformationData(String contextValue, long evidenceEndTime,
                                                                     int timePeriodInDays, String anomalyValue) {
        long from = TimeUtils.calculateStartingTime(evidenceEndTime, timePeriodInDays);
        Map<HistogramKey, Double> histogramMap = new HashMap();
        Map<HistogramKey, Map> additionalInformation = new HashMap();
        HistogramKey anomaly = populate(contextValue, evidenceEndTime, from, histogramMap, additionalInformation,
                anomalyValue);
        SupportingInformationData supportingInformationData;
        if (anomaly != null) {
            supportingInformationData = new SupportingInformationData(histogramMap, anomaly);
        } else {
            supportingInformationData = new SupportingInformationData(histogramMap);
        }
        supportingInformationData.setAdditionalInformation(additionalInformation);
        return supportingInformationData;
    }

    @Override
    public SupportingInformationData createSupportingInformationData(String contextValue, long evidenceEndTime,
                                                                     int timePeriodInDays) {
        return createSupportingInformationData(contextValue, evidenceEndTime, timePeriodInDays, null);
    }

}