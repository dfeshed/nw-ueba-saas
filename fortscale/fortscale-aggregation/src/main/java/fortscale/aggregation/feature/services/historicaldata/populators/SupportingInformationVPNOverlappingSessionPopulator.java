package fortscale.aggregation.feature.services.historicaldata.populators;

import fortscale.aggregation.feature.services.historicaldata.SupportingInformationData;
import fortscale.aggregation.feature.services.historicaldata.SupportingInformationGenericData;
import fortscale.domain.core.Evidence;
import fortscale.domain.core.VpnOverlappingSupportingInformation;
import fortscale.domain.core.VpnSessionOverlap;
import fortscale.domain.historical.data.SupportingInformationDualKey;
import fortscale.domain.historical.data.SupportingInformationKey;
import fortscale.utils.logging.Logger;
import fortscale.utils.time.TimestampUtils;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Supporting information populator class for VPN overlapping sessions
 *
 * @author gils
 * Date: 02/09/2015
 */
@Component
@Scope("prototype")
public class SupportingInformationVPNOverlappingSessionPopulator implements SupportingInformationDataPopulator {

    private static Logger logger = Logger.getLogger(SupportingInformationVPNOverlappingSessionPopulator.class);

    private static final String DURATION_FIELD = "duration";
    private static final String HOSTNAME_FIELD = "hostname";
    private static final String COUNTRY_FIELD = "country";
    private static final String LOCAL_IP_FIELD = "local_ip";
    private static final String READ_BYTES_FIELD = "readbytes";
    private static final String DATA_BUCKET_FIELD = "databucket";

    /**
     * Populates the supporting information data based on the context value, evidence time and anomaly value.
     *
     * @param evidence the evidence
     * @param contextValue the context value
     * @param evidenceEndTime evidence creation time
     * @param timePeriodInDays time period in days
     *
     * @return Supporting information data with/without anomaly value indication
     */
    public SupportingInformationData createSupportingInformationData(Evidence evidence, String contextValue, long evidenceEndTime, Integer timePeriodInDays) {

        Map<SupportingInformationKey, String> vpnSessionIntervalToIp = new HashMap<>();

        VpnOverlappingSupportingInformation vpnOverlappingSupportingInformation = (VpnOverlappingSupportingInformation) evidence.getSupportingInformation();

        if (vpnOverlappingSupportingInformation == null) {
            logger.error("Could not find vpn overlapping supporting information data of evidence {}", evidence.getId());

            return new SupportingInformationGenericData<>(vpnSessionIntervalToIp);
        }

        List<VpnSessionOverlap> vpnSessionOverlapEvents = vpnOverlappingSupportingInformation.getRawEvents();

        if (vpnSessionOverlapEvents == null || vpnSessionOverlapEvents.isEmpty()) {
            logger.error("Could not find any vpn session overlapping event for evidence {}", evidence.getId());

            return new SupportingInformationGenericData<>(vpnSessionIntervalToIp);
        }

        Map<SupportingInformationKey, Map> additionalInformationMap = new HashMap<>();

        for (VpnSessionOverlap vpnSessionOverlap : vpnSessionOverlapEvents) {
            long startTime = vpnSessionOverlap.getDate_time_unix();
            long duration = vpnSessionOverlap.getDuration();

            Long startTimeInMillis = TimestampUtils.convertToMilliSeconds(startTime);
            Long endTimeInMillis = TimestampUtils.convertToMilliSeconds(startTime + duration);

            SupportingInformationKey supportingInformationKey = new SupportingInformationDualKey(Long.toString(startTimeInMillis), Long.toString(endTimeInMillis), vpnSessionOverlap.getSource_ip());

            vpnSessionIntervalToIp.put(supportingInformationKey, vpnSessionOverlap.getSource_ip());

            Map<String, Object> additionalInformationValues = createAdditionalInformationMap(vpnSessionOverlap, duration);

            additionalInformationMap.put(supportingInformationKey, additionalInformationValues);
        }

        SupportingInformationGenericData<String> supportingInformationData = new SupportingInformationGenericData<>(vpnSessionIntervalToIp);

        supportingInformationData.setAdditionalInformation(additionalInformationMap);

        return supportingInformationData;
    }

    private Map<String, Object> createAdditionalInformationMap(VpnSessionOverlap vpnSessionOverlap, long duration) {
        Map<String, Object> additionalInformationValues = new HashMap<>();

        additionalInformationValues.put(DURATION_FIELD, duration);
        additionalInformationValues.put(HOSTNAME_FIELD, vpnSessionOverlap.getHostname());
        additionalInformationValues.put(COUNTRY_FIELD, vpnSessionOverlap.getCountry());
        additionalInformationValues.put(LOCAL_IP_FIELD, vpnSessionOverlap.getLocal_ip());
        additionalInformationValues.put(READ_BYTES_FIELD, vpnSessionOverlap.getReadbytes());
        additionalInformationValues.put(DATA_BUCKET_FIELD, vpnSessionOverlap.getDatabucket());

        return additionalInformationValues;
    }

}