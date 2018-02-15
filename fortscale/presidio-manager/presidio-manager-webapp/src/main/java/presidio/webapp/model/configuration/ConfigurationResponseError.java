package presidio.webapp.model.configuration;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonValue;
import io.swagger.annotations.ApiModelProperty;

import java.util.Objects;

/**
 * ConfigurationResponseError
 */
@javax.annotation.Generated(value = "class io.swagger.codegen.languages.SpringCodegen", date = "2017-08-07T07:15:37.402Z")

public class ConfigurationResponseError {
    /**
     * Hinting mailfunctioning configuration for a specific component. 'security'- one of the system security params is invalid 'dataPipline' - invalid data pipline configurations (i.e. wrong date format) 'general' - other errors
     */
    public enum DomainEnum {
        GENERAL("general"),

        SECURITY("security"),

        DATA_PIPELINE("dataPipeline"),

        OUTPUT_FORWARDING("outputForwarding");


        private String value;

        DomainEnum(String value) {
            this.value = value;
        }

        @Override
        @JsonValue
        public String toString() {
            return String.valueOf(value);
        }

        @JsonCreator
        public static DomainEnum fromValue(String text) {
            for (DomainEnum b : DomainEnum.values()) {
                if (String.valueOf(b.value).equals(text)) {
                    return b;
                }
            }
            return null;
        }
    }

    @JsonProperty("domain")
    private DomainEnum domain = null;

    /**
     * A unique identifier for the error
     */
    public enum ReasonEnum {

        UNSUPPORTED_FIELD_ERROR("unsupportedFieldError"),

        INVALID_PROPERTY("invalidProperty"),

        MISSING_PROPERTY("missingProperty");

        private String value;

        ReasonEnum(String value) {
            this.value = value;
        }

        @Override
        @JsonValue
        public String toString() {
            return String.valueOf(value);
        }

        @JsonCreator
        public static ReasonEnum fromValue(String text) {
            for (ReasonEnum b : ReasonEnum.values()) {
                if (String.valueOf(b.value).equals(text)) {
                    return b;
                }
            }
            return null;
        }
    }

    @JsonProperty("reason")
    private ReasonEnum reason = null;

    @JsonProperty("message")
    private String message = null;

    /**
     * Determines how the client should interpret the location property
     */
    public enum LocationTypeEnum {
        JSON_PATH("jsonPath"),

        PARAMETER("parameter");

        private String value;

        LocationTypeEnum(String value) {
            this.value = value;
        }

        @Override
        @JsonValue
        public String toString() {
            return String.valueOf(value);
        }

        @JsonCreator
        public static LocationTypeEnum fromValue(String text) {
            for (LocationTypeEnum b : LocationTypeEnum.values()) {
                if (String.valueOf(b.value).equals(text)) {
                    return b;
                }
            }
            return null;
        }
    }

    @JsonProperty("locationType")
    private LocationTypeEnum locationType = null;

    @JsonProperty("location")
    private String location = null;

    public ConfigurationResponseError domain(DomainEnum domain) {
        this.domain = domain;
        return this;
    }

    /**
     * Hinting mailfunctioning configuration for a specific component. 'security'- one of the system security params is invalid 'dataPipline' - invalid data pipline configurations (i.e. wrong date format) 'general' - other errors
     *
     * @return domain
     **/
    @ApiModelProperty(example = "dataPipline", value = "Hinting mailfunctioning configuration for a specific component. 'security'- one of the system security params is invalid 'dataPipline' - invalid data pipline configurations (i.e. wrong date format) 'general' - other errors")
    public DomainEnum getDomain() {
        return domain;
    }

    public void setDomain(DomainEnum domain) {
        this.domain = domain;
    }

    public ConfigurationResponseError reason(ReasonEnum reason) {
        this.reason = reason;
        return this;
    }

    /**
     * A unique identifier for the error
     *
     * @return reason
     **/
    @ApiModelProperty(example = "invalidParameter", value = "A unique identifier for the error")
    public ReasonEnum getReason() {
        return reason;
    }

    public void setReason(ReasonEnum reason) {
        this.reason = reason;
    }

    public ConfigurationResponseError message(String message) {
        this.message = message;
        return this;
    }

    /**
     * A description of the error in a human-readable format
     *
     * @return message
     **/
    @ApiModelProperty(example = "Invalid schema value: &quot;dlpfile&quot;. Allowed values: [file, active directory, authentication]", value = "A description of the error in a human-readable format")
    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public ConfigurationResponseError locationType(LocationTypeEnum locationType) {
        this.locationType = locationType;
        return this;
    }

    /**
     * Determines how the client should interpret the location property
     *
     * @return locationType
     **/
    @ApiModelProperty(example = "jsonPath", value = "Determines how the client should interpret the location property")
    public LocationTypeEnum getLocationType() {
        return locationType;
    }

    public void setLocationType(LocationTypeEnum locationType) {
        this.locationType = locationType;
    }

    public ConfigurationResponseError location(String location) {
        this.location = location;
        return this;
    }

    /**
     * The location of the error with interpertation determined by the location type. (e.g. parameter name, json path)
     *
     * @return location
     **/
    @ApiModelProperty(example = "dataPipeline/schemas", value = "The location of the error with interpertation determined by the location type. (e.g. parameter name, json path)")
    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ConfigurationResponseError configurationErrorResponseError = (ConfigurationResponseError) o;
        return Objects.equals(this.domain, configurationErrorResponseError.domain) &&
                Objects.equals(this.reason, configurationErrorResponseError.reason) &&
                Objects.equals(this.message, configurationErrorResponseError.message) &&
                Objects.equals(this.locationType, configurationErrorResponseError.locationType) &&
                Objects.equals(this.location, configurationErrorResponseError.location);
    }

    @Override
    public int hashCode() {
        return Objects.hash(domain, reason, message, locationType, location);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("class ConfigurationResponseError {\n");

        sb.append("    domain: ").append(toIndentedString(domain)).append("\n");
        sb.append("    reason: ").append(toIndentedString(reason)).append("\n");
        sb.append("    message: ").append(toIndentedString(message)).append("\n");
        sb.append("    locationType: ").append(toIndentedString(locationType)).append("\n");
        sb.append("    location: ").append(toIndentedString(location)).append("\n");
        sb.append("}");
        return sb.toString();
    }

    /**
     * Convert the given object to string with each line indented by 4 spaces
     * (except the first line).
     */
    private String toIndentedString(Object o) {
        if (o == null) {
            return "null";
        }
        return o.toString().replace("\n", "\n    ");
    }
}

