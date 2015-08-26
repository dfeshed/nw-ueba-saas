package fortscale.aggregation.feature.services.historicaldata.populators;

import fortscale.domain.core.Evidence;
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
     * @param evidence the evidence which caused the alert
     * @param normalizedUsername normalized username to search for
     * @param startTime start time for the search
     * @param evidenceEndTime end time for the search
     * @param histogramMap the histogram map to populate
     * @param additionalInformation additional information to populate
     * @return HistogramKey representing the anomaly value's key
     */
    protected HistogramKey populate(Evidence evidence, String normalizedUsername, long startTime, long evidenceEndTime,
                                    Map<HistogramKey, Double> histogramMap,
                                    Map<HistogramKey, Map> additionalInformation) {
        List<VpnSession> vpnSessions = vpnService.findByNormalizedUserNameAndCreatedAtEpochBetweenAndDurationExists(
                normalizedUsername, startTime, evidenceEndTime);
        HistogramKey anomaly = null;
        for (VpnSession vpnSession: vpnSessions) {
            HistogramKey key = new HistogramSingleKey(vpnSession.getCreatedAtEpoch() + "");
            histogramMap.put(key, (double)vpnSession.getDataBucket());
            Map<String, Long> info = new HashMap<>();
            info.put(DURATION, (long)vpnSession.getDuration());
            info.put(DOWNLOADED_BYTES, vpnSession.getTotalBytes());
            additionalInformation.put(key, info);
            //if this is the vpnsession that caused the evidence to be created - get its start time as the anomaly key
            if (anomaly == null && vpnSession.getClosedAtEpoch() == evidenceEndTime) {
                anomaly = new HistogramSingleKey(vpnSession.getCreatedAtEpoch() + "");
            }
        }
        return anomaly;
    }

}