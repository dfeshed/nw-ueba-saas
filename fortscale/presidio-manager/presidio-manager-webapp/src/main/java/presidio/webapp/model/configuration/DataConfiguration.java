package presidio.webapp.model.configuration;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModelProperty;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * DataConfiguration
 */
@javax.annotation.Generated(value = "class io.swagger.codegen.languages.SpringCodegen", date = "2017-08-07T07:15:37.402Z")

public class DataConfiguration {
    @JsonProperty("schemas")
    private List<SchemasEnum> schemas = new ArrayList<SchemasEnum>();

    @JsonProperty("startTime")
    private Instant startTime = null;

    public DataConfiguration() {
    }

    public DataConfiguration(List<SchemasEnum> schemas, Instant startTime) {
        this.schemas = schemas;
        this.startTime = startTime;
    }

    public DataConfiguration schemas(List<SchemasEnum> schemas) {
        this.schemas = schemas;
        return this;
    }

    public DataConfiguration addSchemasItem(SchemasEnum schemasItem) {
        this.schemas.add(schemasItem);
        return this;
    }

    /**
     * Get schemas
     *
     * @return schemas
     **/
    @ApiModelProperty(value = "")
    public List<SchemasEnum> getSchemas() {
        return schemas;
    }

    public void setSchemas(List<SchemasEnum> schemas) {
        this.schemas = schemas.stream().filter(schemasEnum -> schemasEnum != null).collect(Collectors.toList());
    }

    public DataConfiguration startTime(Instant startTime) {
        this.startTime = startTime;
        return this;
    }

    /**
     * Date time in ISO-format UTC determining the first event time to be used for anomaly detection
     *
     * @return startTime
     **/
    @ApiModelProperty(example = "2007-12-03T10:15:30.00Z", value = "Date time in ISO-format UTC determining the first event time to be used for anomaly detection")
    public Instant getStartTime() {
        return startTime;
    }

    public void setStartTime(Instant startTime) {
        this.startTime = startTime;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        DataConfiguration dataConfiguration = (DataConfiguration) o;
        return Objects.equals(this.schemas, dataConfiguration.schemas) &&
                Objects.equals(this.startTime, dataConfiguration.startTime);
    }

    @Override
    public int hashCode() {
        return Objects.hash(schemas, startTime);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("class DataConfiguration {\n");

        sb.append("    schemas: ").append(toIndentedString(schemas)).append("\n");
        sb.append("    startTime: ").append(toIndentedString(startTime)).append("\n");
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

