package fortscale.streaming.service.vpn;

import fortscale.domain.events.VpnSession;
import fortscale.domain.schema.VpnEvents;
import fortscale.geoip.GeoIPInfo;
import fortscale.geoip.IpToLocationGeoIPService;
import fortscale.services.event.VpnService;
import fortscale.services.notifications.VpnGeoHoppingNotificationGenerator;
import net.minidev.json.JSONObject;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;

import java.util.ArrayList;
import java.util.List;

import static com.google.common.base.Preconditions.checkNotNull;
import static fortscale.utils.ConversionUtils.convertToBoolean;
import static fortscale.utils.ConversionUtils.convertToLong;
import static fortscale.utils.ConversionUtils.convertToString;

/**
 * Service that receive and event from a vpn input topic, and provides three services:
 * 1. geolocation
 * 2. Buckets calculation
 * 3. session update
 */
@Configurable(preConstruction=true)
public class VpnEnrichService {

    public static Logger logger = LoggerFactory.getLogger(VpnEnrichService.class);

    private VpnEnrichConfig config;

    @Autowired
    IpToLocationGeoIPService ipToLocationGeoIPService;
    @Autowired
    private VpnEvents vpnEvents;
    @Autowired
    VpnService vpnService;
    @Autowired
    private RecordToVpnSessionConverter recordToVpnSessionConverter;
    @Autowired
    private VpnGeoHoppingNotificationGenerator vpnGeoHoppingNotificationGenerator;

    public VpnEnrichService(VpnEnrichConfig config) {
        checkNotNull(config);
        this.config = config;
    }

    public JSONObject processVpnEvent(JSONObject event) {
        checkNotNull(event);
        event = processGeolocation(event);
        event = processDataBuckets(event);
        event = processSessionUpdate(event);
        return event;
    }



    public JSONObject processGeolocation(JSONObject event) {
        VpnGeolocationConfig vpnGeolocationConfig = config.getVpnGeolocationConfig();
        String ipAddress = convertToString(event.get(vpnGeolocationConfig.getIpField()));
        // If the geo ip service is available
        try {
            GeoIPInfo geoIPInfo = ipToLocationGeoIPService.getGeoIPInfo(ipAddress);

            // Write the ip info:  country, city, isp, usageType
            event.put(vpnGeolocationConfig.getCountryFieldName(), geoIPInfo.getCountryName());
            event.put(vpnGeolocationConfig.getCountryIsoCodeFieldName(), geoIPInfo.getCountryISOCode());
            event.put(vpnGeolocationConfig.getRegionFieldName(), geoIPInfo.getRegionName());
            event.put(vpnGeolocationConfig.getCityFieldName(), geoIPInfo.getCityName());
            event.put(vpnGeolocationConfig.getIspFieldName(), geoIPInfo.getISP());
            event.put(vpnGeolocationConfig.getUsageTypeFieldName(), geoIPInfo.getUsageType() != null ? geoIPInfo.getUsageType().getId() : "");
            event.put(vpnGeolocationConfig.getLongtitudeFieldName(), geoIPInfo.getLongitude());
            event.put(vpnGeolocationConfig.getLatitudeFieldName(), geoIPInfo.getLatitude());
        } catch (Exception e) {
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
        if(readBytes == null){
            readBytes = convertToLong(event.get(vpnDataBucketsConfig.getTotalbytesFieldName()));
        }

        // calculate bucket - in case that we don't have duration, we will not add the bucket field and the score will be 0
        if(duration != null ){
            if(duration > 0){
                Long bytePerSec = (Long.valueOf(readBytes)/(20*60 + duration));
                event.put(vpnDataBucketsConfig.getDatabucketFieldName(), bytePerSec);
            }
        }

        return event;
    }

    protected JSONObject processSessionUpdate(JSONObject event) {
        VpnSessionUpdateConfig vpnSessionUpdateConfig = config.getVpnSessionUpdateConfig();

        if(vpnService == null){
            logger.warn("vpnService is null while processing command {}. probably the spring configuration context was not loaded", VpnEnrichService.class);
            return event;
        }
        String countryIsoCodeFieldName = vpnSessionUpdateConfig.getCountryIsoCodeFieldName();
        String longtitudeFieldName = vpnSessionUpdateConfig.getLongtitudeFieldName();
        String latitudeFieldName = vpnSessionUpdateConfig.getLatitudeFieldName();
        String sessionIdFieldName = vpnSessionUpdateConfig.getSessionIdFieldName();
        VpnSession vpnSession = recordToVpnSessionConverter.convert(event, countryIsoCodeFieldName, longtitudeFieldName, latitudeFieldName, sessionIdFieldName);

        // check if failed event
        if(vpnSession.getClosedAt() == null && vpnSession.getCreatedAt() == null){
            //right now we don't use fail status for updating vpn session. There is a JIRA for this (FV-4413).
            return event;
        }

        // validate fields: session-ID or (username and source-IP)
        if (StringUtils.isEmpty(vpnSession.getSessionId()) && (StringUtils.isEmpty(vpnSession.getUsername()) || StringUtils.isEmpty(vpnSession.getSourceIp()))) {
            logger.warn("vpnSession should have either sessionId or username and sourceIP. Original record is: {}", event.toString());
            return event;
        }

        /**
         * when <code>addSessionData</code> is false: if there is a close session event without an open event we drop this session
         * if true: we can create a session without the stat session event as we have all attributes in the close session event.
         */
        Boolean isAddSessionData = convertToBoolean( vpnSessionUpdateConfig.getAddSessionDataFieldName());
        if(vpnSession.getClosedAt() != null && isAddSessionData){
            VpnSession vpnOpenSession = getOpenSessionDataToRecord(vpnSession);
            if(vpnOpenSession == null){
                logger.debug("got close vpn session for non existing or failed session");
                return event;
            } else{
                addOpenSessionDataToRecord(vpnSessionUpdateConfig, event, vpnOpenSession);
            }
        }

        Boolean isRunGeoHopping = convertToBoolean(event.get(vpnSessionUpdateConfig.getRunGeoHoppingFieldName()));
        if(isRunGeoHopping != null && isRunGeoHopping){
            processGeoHopping(vpnSessionUpdateConfig, vpnSession);
        }

        if(vpnSession.getCreatedAt() != null){
            vpnService.createOrUpdateOpenVpnSession(vpnSession);
        } else{
            vpnService.updateCloseVpnSession(vpnSession);
        }

        return event;
    }

    private VpnSession getOpenSessionDataToRecord(VpnSession closeVpnSessionData){
        VpnSession vpnOpenSession = null;
        if(closeVpnSessionData.getSessionId() != null){
            vpnOpenSession = vpnService.findBySessionId(closeVpnSessionData.getSessionId());
        } else{
            vpnOpenSession = vpnService.findByUsernameAndSourceIp(closeVpnSessionData.getUsername(), closeVpnSessionData.getSourceIp());
        }
        return vpnOpenSession;
    }


    private void addOpenSessionDataToRecord(VpnSessionUpdateConfig vpnSessionUpdateConfig, JSONObject event, VpnSession openVpnSessionData){
        if(event.get(vpnEvents.NORMALIZED_USERNAME) == null || event.get(vpnEvents.NORMALIZED_USERNAME).equals("")){
            event.put(vpnEvents.NORMALIZED_USERNAME, openVpnSessionData.getNormalizeUsername());
        }
        if(event.get(vpnEvents.USERNAME) == null || event.get(vpnEvents.USERNAME).equals("")){
            event.put(vpnEvents.USERNAME, openVpnSessionData.getUsername());
        }
        if(event.get(vpnEvents.HOSTNAME) == null || event.get(vpnEvents.HOSTNAME).equals("")){
            event.put(vpnEvents.HOSTNAME, openVpnSessionData.getHostname());
        }
        if(event.get(vpnEvents.SOURCE_IP) == null || event.get(vpnEvents.SOURCE_IP).equals("")){
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
        if(event.get(vpnEvents.LOCAL_IP) == null || event.get(vpnEvents.LOCAL_IP).equals("")){
            event.put(vpnEvents.LOCAL_IP, openVpnSessionData.getLocalIp());
        }
    }

    private void processGeoHopping(VpnSessionUpdateConfig vpnSessionUpdateConfig, VpnSession curVpnSession){
        if(curVpnSession.getClosedAt() == null){
            List<VpnSession> vpnSessions = vpnService.getGeoHoppingVpnSessions(curVpnSession, vpnSessionUpdateConfig.getVpnGeoHoppingCloseSessionThresholdInHours(), vpnSessionUpdateConfig.getVpnGeoHoppingOpenSessionThresholdInHours());
            if(curVpnSession.getGeoHopping()){
                List<VpnSession> notificationList = new ArrayList<>();
                notificationList.add(curVpnSession);
                for(VpnSession vpnSession: vpnSessions){
                    if(!vpnSession.getGeoHopping()){
                        vpnSession.setGeoHopping(true);
                        vpnService.saveVpnSession(vpnSession);
                        notificationList.add(vpnSession);
                    }
                }

                //create notifications for the vpn sessions
                vpnGeoHoppingNotificationGenerator.createNotifications(notificationList);
            }


        }
    }


    public String getOutputTopic() {
        return config.getOutputTopic();
    }

    public String getInputTopic() {
        return config.getInputTopic();
    }

    /**
    * Get the partition key to use for outgoing message envelope for the given event
    */
    public Object getPartitionKey(JSONObject event) {
        checkNotNull(event);
        return event.get(config.getPartitionField());
    }


}
