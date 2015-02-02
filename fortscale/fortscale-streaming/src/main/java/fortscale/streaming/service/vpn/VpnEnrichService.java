package fortscale.streaming.service.vpn;

import fortscale.geoip.GeoIPInfo;
import fortscale.geoip.IpToLocationGeoIPService;
import fortscale.services.ipresolving.IpToHostnameResolver;
import fortscale.streaming.service.ipresolving.EventResolvingConfig;
import net.minidev.json.JSONObject;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.google.common.base.Preconditions.checkNotNull;
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

    public VpnEnrichService(VpnEnrichConfig config) {
        checkNotNull(config);
        this.config = config;
    }

    public JSONObject processGeolocation(JSONObject event) {
        checkNotNull(event);

        // get the configuration for the input topic, if not found skip this event

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
