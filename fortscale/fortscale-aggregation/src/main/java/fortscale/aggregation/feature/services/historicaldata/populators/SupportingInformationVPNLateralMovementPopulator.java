package fortscale.aggregation.feature.services.historicaldata.populators;

import fortscale.aggregation.feature.services.historicaldata.SupportingInformationData;
import fortscale.aggregation.feature.services.historicaldata.SupportingInformationGenericData;
import fortscale.domain.core.Evidence;
import fortscale.domain.core.VpnLateralMovementSupportingInformation;
import fortscale.domain.core.VpnOverlappingSupportingInformation;
import fortscale.domain.core.VpnSessionOverlap;
import fortscale.domain.core.dao.VpnLateralMovement;
import fortscale.domain.historical.data.SupportingInformationDualKey;
import fortscale.domain.historical.data.SupportingInformationKey;
import fortscale.domain.historical.data.SupportingInformationSingleKey;
import fortscale.utils.logging.Logger;
import fortscale.utils.time.TimestampUtils;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Supporting information populator class for VPN lateral movement
 *
 * @author Amir Keren
 * Date: 19/07/2016
 */
@Component
@Scope("prototype")
public class SupportingInformationVPNLateralMovementPopulator implements SupportingInformationDataPopulator {

    private static Logger logger = Logger.getLogger(SupportingInformationVPNLateralMovementPopulator.class);

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
    public SupportingInformationData createSupportingInformationData(Evidence evidence, String contextValue,
			long evidenceEndTime, Integer timePeriodInDays) {
        Map<SupportingInformationKey, String> vpnLateralMovementMap = new HashMap<>();
        VpnLateralMovementSupportingInformation vpnLateralMovementSupportingInformation =
				(VpnLateralMovementSupportingInformation)evidence.getSupportingInformation();
        if (vpnLateralMovementSupportingInformation == null) {
            logger.error("Could not find vpn lateral movement supporting information data of evidence {}",
					evidence.getId());
            return new SupportingInformationGenericData<>(vpnLateralMovementMap);
        }
        VpnLateralMovementSupportingInformation.VPNLateralMovementDTO vpnLateralMovementEvents =
				vpnLateralMovementSupportingInformation.getRawEvents();
        if (vpnLateralMovementEvents == null) {
            logger.error("Could not find any vpn lateral movement events for evidence {}", evidence.getId());
            return new SupportingInformationGenericData<>(vpnLateralMovementMap);
        }
        Map<SupportingInformationKey, Map> additionalInformationMap = new HashMap<>();
		for (VpnSessionOverlap vpnSessionOverlap: vpnLateralMovementEvents.getVpn_session_events()) {
			long endTime = vpnSessionOverlap.getDate_time_unix();
			long duration = vpnSessionOverlap.getDuration();
			Long startTimeInMillis = TimestampUtils.convertToMilliSeconds(endTime - duration);
			Long endTimeInMillis = TimestampUtils.convertToMilliSeconds(endTime);
			SupportingInformationKey supportingInformationKey = new SupportingInformationDualKey(Long.
					toString(startTimeInMillis), Long.toString(endTimeInMillis), vpnSessionOverlap.getUsername());
			vpnLateralMovementMap.put(supportingInformationKey, vpnSessionOverlap.getUsername());
		}
		for (VpnLateralMovement vpnLateralMovement: vpnLateralMovementEvents.getUser_activity_events()) {
			long time = vpnLateralMovement.getEvent_time_utc();
			Long startTimeInMillis = TimestampUtils.convertToMilliSeconds(time);
			SupportingInformationKey supportingInformationKey = new SupportingInformationSingleKey(Long.
					toString(startTimeInMillis), vpnLateralMovement.getDisplay_name());
			vpnLateralMovementMap.put(supportingInformationKey, vpnLateralMovement.getUsername());
		}
        SupportingInformationGenericData<String> supportingInformationData =
				new SupportingInformationGenericData<>(vpnLateralMovementMap);
        supportingInformationData.setAdditionalInformation(additionalInformationMap);
        return supportingInformationData;
    }

}