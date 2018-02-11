package presidio.manager.api.records;


public class ConfigurationBadParamDetails {

    private String domain;

    private String location;

    private String reason;

    private String locationType;

    private String errorMessage;

    public ConfigurationBadParamDetails(String domain, String location, String reason, String locationType, String errorMessage) {
        this.domain = domain;
        this.location = location;
        this.reason = reason;
        this.locationType = locationType;
        this.errorMessage = errorMessage;
    }


    public String getDomain() {
        return domain;
    }

    public String getLocation() {
        return location;
    }

    public String getReason() {
        return reason;
    }

    public String getLocationType() {
        return locationType;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    @Override
    public String toString() {
        return "ConfigurationBadParamDetails{" +
                "domain='" + domain + '\'' +
                ", location='" + location + '\'' +
                ", reason='" + reason + '\'' +
                ", locationType='" + locationType + '\'' +
                ", errorMessage='" + errorMessage + '\'' +
                '}';
    }
}
