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
 * Implementation for Mongo queries populator
 *
 * @author Amir Keren
 * Date: 18/08/2015
 */
@Component
@Scope("prototype")
public class SupportingInformationQueryPopulator implements SupportingInformationDataPopulator {

    @Autowired
    private VpnService vpnService;

    @Override
    public SupportingInformationData createSupportingInformationData(String contextValue, long evidenceEndTime,
                                                                     int timePeriodInDays, String anomalyValue) {
        //TODO - generalize this
        long from = TimeUtils.calculateStartingTime(evidenceEndTime, timePeriodInDays);
        List<VpnSession> vpnSessions = vpnService.findByNormalizedUserNameAndCreatedAtEpochBetweenAndDurationExists(
                contextValue, from, evidenceEndTime);
        Map<HistogramKey, Double> histogramMap = new HashMap();
        Map<HistogramKey, Map> additionalInformation = new HashMap();
        HistogramKey anomaly = null;
        if (anomalyValue != null) {
            anomaly = new HistogramSingleKey(anomalyValue);
        }
        for (VpnSession vpnSession: vpnSessions) {
            HistogramKey key = new HistogramSingleKey(vpnSession.getCreatedAtEpoch() + "");
            histogramMap.put(key, (double)vpnSession.getDataBucket());
            Map<String, Long> info = new HashMap();
            info.put("durationSec", (long)vpnSession.getDuration());
            info.put("totalDownloadBytes", vpnSession.getTotalBytes());
            additionalInformation.put(key, info);
        }
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