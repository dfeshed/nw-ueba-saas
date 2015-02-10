package fortscale.streaming.service.vpn;

import javolution.io.Struct;

/**
 * Created by rans on 03/02/15.
 */
public class VpnSessionUpdateConfig {
    private String countryIsoCodeFieldName;
    private String longtitudeFieldName;
    private String latitudeFieldName;
    private Integer vpnGeoHoppingOpenSessionThresholdInHours;
    private Integer vpnGeoHoppingCloseSessionThresholdInHours;
    private String sessionIdFieldName;
    private String runGeoHoppingFieldName;
    private String addSessionDataFieldName;

    public VpnSessionUpdateConfig(String countryIsoCodeFieldName, String longtitudeFieldName, String latitudeFieldName, int vpnGeoHoppingOpenSessionThresholdInHours, int vpnGeoHoppingCloseSessionThresholdInHours, String sessionIdFieldName, String runGeoHoppingFieldName, String addSessionDataFieldName) {

        this.countryIsoCodeFieldName = countryIsoCodeFieldName;
        this.longtitudeFieldName = longtitudeFieldName;
        this.latitudeFieldName = latitudeFieldName;
        this.vpnGeoHoppingOpenSessionThresholdInHours = vpnGeoHoppingOpenSessionThresholdInHours;
        this.vpnGeoHoppingCloseSessionThresholdInHours = vpnGeoHoppingCloseSessionThresholdInHours;
        this.sessionIdFieldName = sessionIdFieldName;
        this.runGeoHoppingFieldName = runGeoHoppingFieldName;
        this.addSessionDataFieldName = addSessionDataFieldName;
    }

    public String getCountryIsoCodeFieldName() {
        return countryIsoCodeFieldName;
    }

    public void setCountryIsoCodeFieldName(String countryIsoCodeFieldName) {
        this.countryIsoCodeFieldName = countryIsoCodeFieldName;
    }

    public String getLongtitudeFieldName() {
        return longtitudeFieldName;
    }

    public void setLongtitudeFieldName(String longtitudeFieldName) {
        this.longtitudeFieldName = longtitudeFieldName;
    }

    public String getLatitudeFieldName() {
        return latitudeFieldName;
    }

    public void setLatitudeFieldName(String latitudeFieldName) {
        this.latitudeFieldName = latitudeFieldName;
    }

    public Integer getVpnGeoHoppingOpenSessionThresholdInHours() {
        return vpnGeoHoppingOpenSessionThresholdInHours;
    }

    public void setVpnGeoHoppingOpenSessionThresholdInHours(Integer vpnGeoHoppingOpenSessionThresholdInHours) {
        this.vpnGeoHoppingOpenSessionThresholdInHours = vpnGeoHoppingOpenSessionThresholdInHours;
    }

    public Integer getVpnGeoHoppingCloseSessionThresholdInHours() {
        return vpnGeoHoppingCloseSessionThresholdInHours;
    }

    public void setVpnGeoHoppingCloseSessionThresholdInHours(Integer vpnGeoHoppingCloseSessionThresholdInHours) {
        this.vpnGeoHoppingCloseSessionThresholdInHours = vpnGeoHoppingCloseSessionThresholdInHours;
    }

    public String getSessionIdFieldName() {
        return sessionIdFieldName;
    }

    public void setSessionIdFieldName(String sessionIdFieldName) {
        this.sessionIdFieldName = sessionIdFieldName;
    }

    public String getRunGeoHoppingFieldName() {
        return runGeoHoppingFieldName;
    }

    public void setRunGeoHoppingFieldName(String runGeoHoppingFieldName) {
        this.runGeoHoppingFieldName = runGeoHoppingFieldName;
    }

    public String getAddSessionDataFieldName() {
        return addSessionDataFieldName;
    }

    public void setAddSessionDataFieldName(String addSessionDataFieldName) {
        this.addSessionDataFieldName = addSessionDataFieldName;
    }
}
