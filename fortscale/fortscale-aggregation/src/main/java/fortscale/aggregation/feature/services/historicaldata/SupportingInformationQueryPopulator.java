package fortscale.aggregation.feature.services.historicaldata;

import fortscale.domain.core.SupportingInformationData;
import fortscale.domain.events.VpnSession;
import fortscale.domain.histogram.HistogramKey;
import fortscale.domain.histogram.HistogramSingleKey;
import fortscale.services.event.VpnService;
import fortscale.utils.time.TimeUtils;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Implementation for Mongo queries populator
 *
 * @author Amir Keren
 * Date: 18/08/2015
 */
public class SupportingInformationQueryPopulator implements SupportingInformationDataPopulator {

    @Autowired
    private VpnService vpnService;

    @Override
    public SupportingInformationData createSupportingInformationData(String contextValue, long evidenceEndTime,
                                                                     int timePeriodInDays, String anomalyValue) {
        long from = TimeUtils.calculateStartingTime(evidenceEndTime, timePeriodInDays);
        List<VpnSession> vpnSessions = vpnService.findByUsernameAndCreatedAtEpochBetweenAndDurationExists(contextValue,
                from, evidenceEndTime);
        Map<HistogramKey, Double> histogramMap = new HashMap();
        for (VpnSession vpnSession: vpnSessions) {
            //TODO - what about duration, total bytes?
            HistogramKey key = new HistogramSingleKey(vpnSession.getCreatedAtEpoch() + "");
            histogramMap.put(key, (double)vpnSession.getDataBucket());
        }
        HistogramKey anomalyHistogramKey = new HistogramSingleKey(anomalyValue);
        return new SupportingInformationData(histogramMap, anomalyHistogramKey);
    }

    @Override
    public SupportingInformationData createSupportingInformationData(String contextValue, long evidenceEndTime,
                                                                     int timePeriodInDays) {
        long from = TimeUtils.calculateStartingTime(evidenceEndTime, timePeriodInDays);
        List<VpnSession> vpnSessions = vpnService.findByUsernameAndCreatedAtEpochBetweenAndDurationExists(contextValue,
                from, evidenceEndTime);
        Map<HistogramKey, Double> histogramMap = new HashMap();
        for (VpnSession vpnSession: vpnSessions) {
            //TODO - what about duration, total bytes?
            HistogramKey key = new HistogramSingleKey(vpnSession.getCreatedAtEpoch() + "");
            histogramMap.put(key, (double)vpnSession.getDataBucket());
        }
        return new SupportingInformationData(histogramMap);
    }

}