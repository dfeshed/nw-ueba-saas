package presidio.manager.api.records;


public class ConfigurationBadParamDetails {

    private String fieldName;

    private String message;

    private String reason;

    private String locationType;

    private String location;

    public ConfigurationBadParamDetails(String fieldName, String message, String reason, String locationType, String location) {
        this.fieldName = fieldName;
        this.message = message;
        this.reason = reason;
        this.locationType = locationType;
        this.location = location;
    }

    public String getFieldName() {
        return fieldName;
    }

    public String getMessage() {
        return message;
    }

    public String getReason() {
        return reason;
    }

    public String getLocationType() {
        return locationType;
    }
    public String getLocation() {
        return location;
    }
}
