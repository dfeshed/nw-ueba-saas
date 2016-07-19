package fortscale.aggregation.feature.services.historicaldata.populators;

import fortscale.aggregation.feature.services.historicaldata.SupportingInformationData;
import fortscale.aggregation.feature.services.historicaldata.SupportingInformationGenericData;
import fortscale.domain.core.Evidence;
import fortscale.domain.core.VpnLateralMovementSupportingInformation;
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

	private static final String TARGET_MACHINE = "target_machine";
	private static final String DATA_SOURCE = "data_source";
	private static final String DISPLAY_NAME = "display_name";
	private static final String ENTITY_ID = "entity_id";
	private static final String VPN_SESSION = "vpn_session";
	private static final String NORMALIZED_USERNAME = "normalized_username";

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
			String displayName = vpnSessionOverlap.getDisplay_name();
			if (displayName == null) {
				displayName = vpnSessionOverlap.getUsername();
			}
			SupportingInformationKey supportingInformationKey = new SupportingInformationDualKey(Long.
					toString(startTimeInMillis), Long.toString(endTimeInMillis), vpnSessionOverlap.getUsername());
			vpnLateralMovementMap.put(supportingInformationKey, vpnSessionOverlap.getUsername());
			Map<String, Object> additionalInformationValues = new HashMap<>();
			additionalInformationValues.put(TARGET_MACHINE, "");
			additionalInformationValues.put(DATA_SOURCE, VPN_SESSION);
			additionalInformationValues.put(DISPLAY_NAME, displayName);
			additionalInformationValues.put(NORMALIZED_USERNAME, evidence.getEntityName());
			additionalInformationValues.put(ENTITY_ID, evidence.getEntityName());
			additionalInformationMap.put(supportingInformationKey, additionalInformationValues);
		}
		for (VpnLateralMovement vpnLateralMovement: vpnLateralMovementEvents.getUser_activity_events()) {
			long time = vpnLateralMovement.getEvent_time_utc();
			Long startTimeInMillis = TimestampUtils.convertToMilliSeconds(time);
			String displayName = vpnLateralMovement.getDisplay_name();
			if (displayName == null) {
				displayName = vpnLateralMovement.getUsername();
			}
			SupportingInformationKey supportingInformationKey = new SupportingInformationSingleKey(Long.
					toString(startTimeInMillis), vpnLateralMovement.getUsername());
			vpnLateralMovementMap.put(supportingInformationKey, vpnLateralMovement.getUsername());
			Map<String, Object> additionalInformationValues = new HashMap<>();
			additionalInformationValues.put(TARGET_MACHINE, vpnLateralMovement.getNormalized_dst_machine());
			additionalInformationValues.put(DATA_SOURCE, vpnLateralMovement.getData_source());
			additionalInformationValues.put(DISPLAY_NAME, displayName);
			additionalInformationValues.put(NORMALIZED_USERNAME, vpnLateralMovement.getNormalized_username());
			additionalInformationValues.put(ENTITY_ID, vpnLateralMovement.getEntity_id());
			additionalInformationMap.put(supportingInformationKey, additionalInformationValues);
		}
        SupportingInformationGenericData<String> supportingInformationData =
				new SupportingInformationGenericData<>(vpnLateralMovementMap);
        supportingInformationData.setAdditionalInformation(additionalInformationMap);
        return supportingInformationData;
    }

}