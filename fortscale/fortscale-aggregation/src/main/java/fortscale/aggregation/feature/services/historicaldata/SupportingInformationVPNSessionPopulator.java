package fortscale.aggregation.feature.services.historicaldata;

import fortscale.domain.events.VpnSession;
import fortscale.domain.histogram.HistogramKey;
import fortscale.domain.histogram.HistogramSingleKey;
import fortscale.services.event.VpnService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Implementation for VPN Session queries populator
 *
 * @author Amir Keren
 * Date: 18/08/2015
 */
@Component
@Scope("prototype")
public class SupportingInformationVPNSessionPopulator extends SupportingInformationQueryPopulator {

    private final String DURATION = "durationSec";
    private final String DOWNLOADED_BYTES = "totalDownloadBytes";

    @Autowired
    private VpnService vpnService;

    /**
     *
     * This method populates the histogram with vpnsession information
     *
     * @param contextValue
     * @param evidenceEndTime
     * @param from
     * @param histogramMap
     * @param additionalInformation
     * @param anomalyValue
     * @return HistogramKey representing the anomaly value's key
     */
    protected HistogramKey populate(String contextValue, long evidenceEndTime, long from,
                                    Map<HistogramKey, Double> histogramMap,
                                    Map<HistogramKey, Map> additionalInformation, String anomalyValue) {
        List<VpnSession> vpnSessions = vpnService.findByNormalizedUserNameAndCreatedAtEpochBetweenAndDurationExists(
                contextValue, from, evidenceEndTime);
        for (VpnSession vpnSession: vpnSessions) {
            HistogramKey key = new HistogramSingleKey(vpnSession.getClosedAtEpoch() + "");
            histogramMap.put(key, (double)vpnSession.getDataBucket());
            Map<String, Long> info = new HashMap();
            info.put(DURATION, (long)vpnSession.getDuration());
            info.put(DOWNLOADED_BYTES, vpnSession.getTotalBytes());
            additionalInformation.put(key, info);
        }
        return new HistogramSingleKey(evidenceEndTime + "");
    }

}