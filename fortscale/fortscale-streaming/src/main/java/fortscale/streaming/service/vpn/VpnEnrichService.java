package fortscale.streaming.service.vpn;

import fortscale.domain.events.VpnSession;
import fortscale.domain.events.dao.ComputerLoginEventRepository;
import fortscale.domain.schema.VpnEvents;
import fortscale.geoip.GeoIPService;
import fortscale.geoip.IGeoIPInfo;
import fortscale.services.event.VpnService;
import fortscale.services.notifications.VpnGeoHoppingNotificationGenerator;
import fortscale.streaming.service.vpn.metrics.VpnEnrichServiceMetrics;
import net.minidev.json.JSONObject;
import net.minidev.json.JSONStyle;
import org.apache.commons.lang3.StringUtils;
import org.apache.samza.system.OutgoingMessageEnvelope;
import org.apache.samza.system.SystemStream;
import org.apache.samza.task.MessageCollector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.beans.factory.annotation.Value;

import java.util.ArrayList;
import java.util.List;

import static com.google.common.base.Preconditions.checkNotNull;
import static fortscale.utils.ConversionUtils.*;

/**
 * Service that receive and event from a vpn input topic, and provides three services:
 * 1. geolocation
 * 2. Buckets calculation
 * 3. session update
 */
@Configurable(preConstruction = true) public class VpnEnrichService {

	public static Logger logger = LoggerFactory.getLogger(VpnEnrichService.class);

	private VpnEnrichConfig config;

	@Autowired private GeoIPService multiProviderGeoIpService;
	@Autowired private VpnEvents vpnEvents;
	@Autowired private VpnService vpnService;
	@Autowired private ComputerLoginEventRepository computerLoginEventRepository;
	@Autowired private RecordToVpnSessionConverter recordToVpnSessionConverter;
	@Autowired private VpnGeoHoppingNotificationGenerator vpnGeoHoppingNotificationGenerator;

	@Value("${fortscale.bdp.run}")
	private boolean isBDPRunning;
	@Value("${collection.evidence.notification.topic}")
	private String evidenceNotificationTopic;
	@Value("${vpn.ip.pool.topic.name}")
	private String vpnIpPoolTopic;

	Boolean isResolveIp;
	Boolean dropCloseEventWhenOpenMissingAndSessionDataIsNeeded;

	public VpnEnrichService(VpnEnrichConfig config) {
		checkNotNull(config);
		this.config = config;

		if (config.getVpnSessionUpdateConfig() != null) {
			isResolveIp = convertToBoolean(config.getVpnSessionUpdateConfig().getResolveIpFieldName());
			dropCloseEventWhenOpenMissingAndSessionDataIsNeeded = convertToBoolean(config.getVpnSessionUpdateConfig().getDropCloseEventWhenOpenMissingFieldName());
		}

	}

	public JSONObject processVpnEvent(JSONObject event, MessageCollector collector) {
		checkNotNull(event);

		if (config.getVpnGeolocationConfig() != null)
			event = processGeolocation(event);
		if (config.getVpnDataBucketsConfig() != null)
			event = processDataBuckets(event);
		if (config.getVpnSessionUpdateConfig() != null)
			event = processSessionUpdate(event, collector);

		return event;
	}

	public JSONObject processGeolocation(JSONObject event) {
		final VpnEnrichServiceMetrics metrics = config.getMetrics();
		VpnGeolocationConfig vpnGeolocationConfig = config.getVpnGeolocationConfig();
		String ipAddress = convertToString(event.get(vpnGeolocationConfig.getIpField()));
		// If the geo ip service is available
		try {
			IGeoIPInfo geoIPInfo = multiProviderGeoIpService.getGeoIPInfo(ipAddress);

			// Write the ip info:  country, city, isp, usageType
			event.put(vpnGeolocationConfig.getCountryFieldName() != null ? vpnGeolocationConfig.getCountryFieldName() : "missingCountryFieldName", geoIPInfo.getCountryName() != null ? geoIPInfo.getCountryName() : "");
			event.put(vpnGeolocationConfig.getCountryIsoCodeFieldName() != null ? vpnGeolocationConfig.getCountryIsoCodeFieldName() : "missingIsoCodeFieldName", geoIPInfo.getCountryISOCode() != null ? geoIPInfo.getCountryISOCode() : "");
			event.put(vpnGeolocationConfig.getRegionFieldName() != null ? vpnGeolocationConfig.getRegionFieldName() : "missingRegionFieldName", geoIPInfo.getRegionName() != null ? geoIPInfo.getRegionName() : "");
			event.put(vpnGeolocationConfig.getCityFieldName() != null ? vpnGeolocationConfig.getCityFieldName() : "missingCityFieldName", geoIPInfo.getCityName() != null ? geoIPInfo.getCityName() : "");
			event.put(vpnGeolocationConfig.getIspFieldName() != null ? vpnGeolocationConfig.getIspFieldName() : "missingIspFieldName", geoIPInfo.getISP() != null ? geoIPInfo.getISP() : "");
			event.put(vpnGeolocationConfig.getUsageTypeFieldName() != null ? vpnGeolocationConfig.getUsageTypeFieldName() : "missingUsageTypeFieldName", geoIPInfo.getUsageType() != null ? geoIPInfo.getUsageType().getId() : "");
			//event.put(vpnGeolocationConfig.getLongtitudeFieldName() != null ? vpnGeolocationConfig.getLongtitudeFieldName() : "missinglongtitudeFieldName", geoIPInfo.getLongitude());
			//event.put(vpnGeolocationConfig.getLatitudeFieldName() != null ? vpnGeolocationConfig.getLatitudeFieldName() : "missingLatitudeFieldName", geoIPInfo.getLatitude());
		} catch (Exception e) {
			++metrics.geoToIpResolvingFailures;
			logger.warn("error resolving geo2ip for {}, exception: {}", ipAddress, e.toString());
		}

		return event;
	}

	public JSONObject processDataBuckets(JSONObject event) {
		VpnDataBucketsConfig vpnDataBucketsConfig = config.getVpnDataBucketsConfig();
		// get duration
		Long duration = convertToLong(event.get(vpnDataBucketsConfig.getDurationFieldName()));

		// get bytes (get "total" if there are no "read" bytes)
		Long readBytes = convertToLong(event.get(vpnDataBucketsConfig.getReadbytesFieldName()));
		if (readBytes == null) {
			readBytes = convertToLong(event.get(vpnDataBucketsConfig.getTotalbytesFieldName()));
		}

		// calculate bucket - in case that we don't have duration, we will not add the bucket field and the score will be 0
		if (duration != null) {
			if (duration > 0) {
				Long bytePerSec = (Long.valueOf(readBytes) / (20 * 60 + duration));
				event.put(vpnDataBucketsConfig.getDatabucketFieldName(), bytePerSec);
			}
		}

		return event;
	}

	protected JSONObject processSessionUpdate(JSONObject event, MessageCollector collector) {
		final VpnEnrichServiceMetrics metrics = config.getMetrics();
		VpnSessionUpdateConfig vpnSessionUpdateConfig = config.getVpnSessionUpdateConfig();

		if (vpnService == null) {
			logger.warn("vpnService is null while processing command {}. probably the spring configuration context was not loaded", VpnEnrichService.class);
			return event;
		}
		String countryIsoCodeFieldName = vpnSessionUpdateConfig.getCountryIsoCodeFieldName();
		String longtitudeFieldName = vpnSessionUpdateConfig.getLongtitudeFieldName();
		String latitudeFieldName = vpnSessionUpdateConfig.getLatitudeFieldName();
		String sessionIdFieldName = vpnSessionUpdateConfig.getSessionIdFieldName();

		VpnSession vpnSession = recordToVpnSessionConverter.convert(event, countryIsoCodeFieldName, longtitudeFieldName, latitudeFieldName, sessionIdFieldName);

		// check if failed event
		if (vpnSession.getClosedAt() == null && vpnSession.getCreatedAt() == null) {
			++metrics.failedEvents;
			//right now we don't use fail status for updating vpn session. There is a JIRA for this (FV-4413).
			return event;
		}

		// validate fields: session-ID or (username and source-IP)
		if (StringUtils.isEmpty(vpnSession.getSessionId()) && (StringUtils.isEmpty(vpnSession.getUsername()) || StringUtils.isEmpty(vpnSession.getSourceIp()))) {
			logger.warn("vpnSession should have either sessionId or username and sourceIP. Original record is: {}", event.toString());
			++metrics.eventsValidationFailures;
			return event;
		}

		/**
		 * when <code>addSessionData</code> is false: if there is a close session event without an open event we drop this session
		 * if true: we can create a session without the stat session event as we have all attributes in the close session event.
		 */
		Boolean isAddSessionData = convertToBoolean(vpnSessionUpdateConfig.getAddSessionDataFieldName());
		if (vpnSession.getClosedAt() != null && isAddSessionData) {
			VpnSession vpnOpenSession = vpnService.findOpenVpnSession(vpnSession);
			if (vpnOpenSession == null) {
				++metrics.closedSessionsForNonExistingOrFailedSessions;
				logger.warn("got close vpn session for non existing or failed session");
				if (dropCloseEventWhenOpenMissingAndSessionDataIsNeeded) {
                    logger.warn("keep the closed session as is");
					//There is no vpnOpenSession ==> skip this event.
					logger.warn("return the close event as is");
					++metrics.droppedClosedEvents;
					return event;
				} else if (isResolveIp) {
					cleanSourceIpInfoFromEvent(event);
				}
			} else {
				addOpenSessionDataToRecord(vpnSessionUpdateConfig, event, vpnOpenSession);
			}
		}

		Boolean isRunGeoHopping = convertToBoolean(event.get(vpnSessionUpdateConfig.getRunGeoHoppingFieldName()), true);
		if (isRunGeoHopping != null && isRunGeoHopping) {
			sendEvidence(processGeoHopping(vpnSessionUpdateConfig, vpnSession), collector);
		}

		if (vpnSession.getCreatedAt() != null) {
			vpnService.createOpenVpnSession(vpnSession);
			++metrics.openSessions;
		} else {
			//update and get the completed session ( the session with the closed and start , duration etc ...)
			VpnSession completedSession = vpnService.updateCloseVpnSession(vpnSession);

			if (completedSession != null ) {
				//update the Computer login with the session closed (in case the local ip have resolving during the session)
				computerLoginEventRepository.updateResolvingExpireDueToVPNSessionEnd(completedSession.getLocalIp(), completedSession.getCreatedAtEpoch(), completedSession.getClosedAtEpoch());

				//write the ip to the vpn pool topic -
				sendIpToVpnTopicPool(completedSession.getLocalIp(), collector);
			}
			else {
				++metrics.completedSessions;
			}


		}

		return event;
	}

	private void sendEvidence(JSONObject evidence, MessageCollector collector) {
		final VpnEnrichServiceMetrics metrics = config.getMetrics();
		if (evidence != null && collector != null) {
			try {
				OutgoingMessageEnvelope output = new OutgoingMessageEnvelope(new SystemStream("kafka",
					   evidenceNotificationTopic), evidence.get("index"), evidence.toJSONString(JSONStyle.NO_COMPRESS));
				collector.send(output);
			} catch (Exception e) {
				logger.warn("error creating evidence for {}, exception: {}", evidence, e.toString());
			}
			++metrics.sentEvidences;
		}
	}

	private void sendIpToVpnTopicPool(String ip , MessageCollector collector)
	{
		try {
			OutgoingMessageEnvelope output = new OutgoingMessageEnvelope(new SystemStream("kafka",
					vpnIpPoolTopic), ip, "{ip:"+ip+"}");
			collector.send(output);
		} catch (Exception e) {
			logger.warn("error sending ip - {} to vpn pool topic, exception: {}", ip, e.toString());
		}

	}

	private void cleanSourceIpInfoFromEvent(JSONObject event) {
		final VpnEnrichServiceMetrics metrics = config.getMetrics();
		VpnSessionUpdateConfig vpnSessionUpdateConfig = config.getVpnSessionUpdateConfig();
		event.put(vpnEvents.SOURCE_IP, "");
		event.put(vpnEvents.CITY, "");
		event.put(vpnEvents.COUNTRY, "");
		event.put(vpnSessionUpdateConfig.getCountryIsoCodeFieldName(), "");
		event.put(vpnEvents.ISP, "");
		event.put(vpnEvents.IPUSAGE, "");
		event.put(vpnEvents.REGION, "");
		event.put(vpnSessionUpdateConfig.getLongtitudeFieldName(), null);
		event.put(vpnSessionUpdateConfig.getLatitudeFieldName(), null);

		++metrics.cleanedEvents;
	}

	private void addOpenSessionDataToRecord(VpnSessionUpdateConfig vpnSessionUpdateConfig, JSONObject event,
			VpnSession openVpnSessionData) {
		if (event.get(vpnEvents.USERNAME) == null || event.get(vpnEvents.USERNAME).equals("")) {
			event.put(vpnEvents.USERNAME, openVpnSessionData.getUsername());
		}
		if (event.get(vpnEvents.HOSTNAME) == null || event.get(vpnEvents.HOSTNAME).equals("")) {
			event.put(vpnEvents.HOSTNAME, openVpnSessionData.getHostname());
		}
		//when isResolveIp=true => need to override all those fields from open session to close session
		if (event.get(vpnEvents.SOURCE_IP) == null || event.get(vpnEvents.SOURCE_IP).equals("") || isResolveIp) {
			event.put(vpnEvents.SOURCE_IP, openVpnSessionData.getSourceIp());
			event.put(vpnEvents.CITY, openVpnSessionData.getCity());
			event.put(vpnEvents.COUNTRY, openVpnSessionData.getCountry());
			event.put(vpnSessionUpdateConfig.getCountryIsoCodeFieldName(), openVpnSessionData.getCountryIsoCode());
			event.put(vpnEvents.ISP, openVpnSessionData.getIsp());
			event.put(vpnEvents.IPUSAGE, openVpnSessionData.getIspUsage());
			event.put(vpnEvents.REGION, openVpnSessionData.getRegion());
			event.put(vpnSessionUpdateConfig.getLongtitudeFieldName(), openVpnSessionData.getLongtitude());
			event.put(vpnSessionUpdateConfig.getLatitudeFieldName(), openVpnSessionData.getLatitude());
		}
		if (event.get(vpnEvents.LOCAL_IP) == null || event.get(vpnEvents.LOCAL_IP).equals("")) {
			event.put(vpnEvents.LOCAL_IP, openVpnSessionData.getLocalIp());
		}
	}

	private JSONObject processGeoHopping(VpnSessionUpdateConfig vpnSessionUpdateConfig, VpnSession curVpnSession) {
		if (curVpnSession.getClosedAt() == null) {
			List<VpnSession> vpnSessions = vpnService.getGeoHoppingVpnSessions(curVpnSession, vpnSessionUpdateConfig.getVpnGeoHoppingCloseSessionThresholdInHours(), vpnSessionUpdateConfig.getVpnGeoHoppingOpenSessionThresholdInHours());
			if (curVpnSession.getGeoHopping()) {
				// put curVpnSession first in the list - important for createIndicator
				List<VpnSession> notificationList = new ArrayList<>();
				notificationList.add(curVpnSession);
				for (VpnSession vpnSession : vpnSessions) {
					notificationList.add(vpnSession);
				}
				//create notifications for the vpn sessions
				if (!isBDPRunning) {
					return vpnGeoHoppingNotificationGenerator.createIndicator(notificationList);
				}
				vpnGeoHoppingNotificationGenerator.createNotification(notificationList);
			}

		}
		return null;

	}

	public String getUsernameFieldName() {
		return config.getUsernameFieldName();
	}

	public String getOutputTopic() {
		return config.getOutputTopic();
	}


	/**
	 * Get the partition key to use for outgoing message envelope for the given event
	 */
	public Object getPartitionKey(JSONObject event) {
		checkNotNull(event);
		return event.get(config.getPartitionField());
	}

}
