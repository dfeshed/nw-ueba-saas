package fortscale.streaming.service.vpn;

/**
 * Created by rans on 02/02/15.
 */
public class VpnGeolocationConfig {
    // Geo-location fields:
    private String ipField;
    private String countryFieldName;
    private String countryIsoCodeFieldName;
    private String regionFieldName;
    private String cityFieldName;
    private String ispFieldName;
    private String usageTypeFieldName;
    private String longtitudeFieldName;
    private String latitudeFieldName;

    public VpnGeolocationConfig(String ipField, String countryFieldName, String countryIsoCodeFieldName, String regionFieldName, String cityFieldName, String ispFieldName, String usageTypeFieldName, String longtitudeFieldName, String latitudeFieldName) {
        this.ipField = ipField;
        this.countryFieldName = countryFieldName;
        this.countryIsoCodeFieldName = countryIsoCodeFieldName;
        this.regionFieldName = regionFieldName;
        this.cityFieldName = cityFieldName;
        this.ispFieldName = ispFieldName;
        this.usageTypeFieldName = usageTypeFieldName;
        this.longtitudeFieldName = longtitudeFieldName;
        this.latitudeFieldName = latitudeFieldName;
    }

    public String getIpField() {
        return ipField;
    }

    public void setIpField(String ipField) {
        this.ipField = ipField;
    }

    public String getCountryFieldName() {
        return countryFieldName;
    }

    public void setCountryFieldName(String countryFieldName) {
        this.countryFieldName = countryFieldName;
    }

    public String getCountryIsoCodeFieldName() {
        return countryIsoCodeFieldName;
    }

    public void setCountryIsoCodeFieldName(String countryIsoCodeFieldName) {
        this.countryIsoCodeFieldName = countryIsoCodeFieldName;
    }

    public String getRegionFieldName() {
        return regionFieldName;
    }

    public void setRegionFieldName(String regionFieldName) {
        this.regionFieldName = regionFieldName;
    }

    public String getCityFieldName() {
        return cityFieldName;
    }

    public void setCityFieldName(String cityFieldName) {
        this.cityFieldName = cityFieldName;
    }

    public String getIspFieldName() {
        return ispFieldName;
    }

    public void setIspFieldName(String ispFieldName) {
        this.ispFieldName = ispFieldName;
    }

    public String getUsageTypeFieldName() {
        return usageTypeFieldName;
    }

    public void setUsageTypeFieldName(String usageTypeFieldName) {
        this.usageTypeFieldName = usageTypeFieldName;
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
}
