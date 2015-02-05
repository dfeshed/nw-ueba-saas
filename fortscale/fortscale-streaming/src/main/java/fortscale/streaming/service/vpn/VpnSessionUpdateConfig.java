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
    private Boolean runGeoHopping;
    private Boolean addSessionData;

    public VpnSessionUpdateConfig(String countryIsoCodeFieldName, String longtitudeFieldName, String latitudeFieldName, int vpnGeoHoppingOpenSessionThresholdInHours, int vpnGeoHoppingCloseSessionThresholdInHours, String sessionIdFieldName, boolean runGeoHopping, boolean addSessionData) {

        this.countryIsoCodeFieldName = countryIsoCodeFieldName;
        this.longtitudeFieldName = longtitudeFieldName;
        this.latitudeFieldName = latitudeFieldName;
        this.vpnGeoHoppingOpenSessionThresholdInHours = vpnGeoHoppingOpenSessionThresholdInHours;
        this.vpnGeoHoppingCloseSessionThresholdInHours = vpnGeoHoppingCloseSessionThresholdInHours;
        this.sessionIdFieldName = sessionIdFieldName;
        this.runGeoHopping = runGeoHopping;
        this.addSessionData = addSessionData;
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

    public Boolean getRunGeoHopping() {
        return runGeoHopping;
    }

    public void setRunGeoHopping(Boolean runGeoHopping) {
        this.runGeoHopping = runGeoHopping;
    }

    public Boolean getAddSessionData() {
        return addSessionData;
    }

    public void setAddSessionData(Boolean addSessionData) {
        this.addSessionData = addSessionData;
    }
}
