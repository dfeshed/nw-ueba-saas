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

    protected abstract void populate(String contextValue, long evidenceEndTime, long from, Map<HistogramKey, Double> histogramMap, Map<HistogramKey, Map> additionalInformation);

    @Override
    public SupportingInformationData createSupportingInformationData(String contextValue, long evidenceEndTime,
                                                                     int timePeriodInDays, String anomalyValue) {
        long from = TimeUtils.calculateStartingTime(evidenceEndTime, timePeriodInDays);
        Map<HistogramKey, Double> histogramMap = new HashMap();
        Map<HistogramKey, Map> additionalInformation = new HashMap();
        HistogramKey anomaly = null;
        SupportingInformationData supportingInformationData;
        if (anomalyValue != null) {
            anomaly = new HistogramSingleKey(anomalyValue);
        }
        populate(contextValue, evidenceEndTime, from, histogramMap, additionalInformation);
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