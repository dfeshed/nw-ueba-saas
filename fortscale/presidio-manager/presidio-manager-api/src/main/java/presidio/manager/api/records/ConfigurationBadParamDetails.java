package presidio.manager.api.records;


public class ConfigurationBadParamDetails {

    private String fieldName;

    private String message;

    private String reason;

    private String locationType;

    public ConfigurationBadParamDetails(String fieldName, String message, String reason, String locationType) {
        this.fieldName = fieldName;
        this.message = message;
        this.reason = reason;
        this.locationType = locationType;
    }
}
