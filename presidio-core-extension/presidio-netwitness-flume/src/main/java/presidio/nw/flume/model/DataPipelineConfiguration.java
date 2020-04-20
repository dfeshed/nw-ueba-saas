package presidio.nw.flume.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSetter;
import io.swagger.annotations.ApiModelProperty;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;


public class DataPipelineConfiguration {
    @JsonProperty("schemas")
    private List<SchemaEnum> schemas = new ArrayList<SchemaEnum>();

    @JsonProperty("startTime")
    private Instant startTime = null;

    public DataPipelineConfiguration() {
    }

    public DataPipelineConfiguration(List<SchemaEnum> schemas, Instant startTime) {
        this.schemas = schemas;
        this.startTime = startTime;
    }

    public DataPipelineConfiguration schemas(List<SchemaEnum> schemas) {
        this.schemas = schemas;
        return this;
    }

    public DataPipelineConfiguration addSchemasItem(SchemaEnum schemasItem) {
        this.schemas.add(schemasItem);
        return this;
    }

    /**
     * Get schemas
     *
     * @return schemas
     **/
    public List<SchemaEnum> getSchemas() {
        return schemas;
    }

    public void setSchemas(List<SchemaEnum> schemas) {
        this.schemas = schemas.stream().filter(Objects::nonNull).collect(Collectors.toList());
    }

    public DataPipelineConfiguration startTime(Instant startTime) {
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

    @JsonSetter
    private void setStartTime(String startTime) {
        this.startTime = Instant.parse(startTime);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        DataPipelineConfiguration dataPipelineConfiguration = (DataPipelineConfiguration) o;
        return Objects.equals(this.schemas, dataPipelineConfiguration.schemas) &&
                Objects.equals(this.startTime, dataPipelineConfiguration.startTime);
    }

    @Override
    public int hashCode() {
        return Objects.hash(schemas, startTime);
    }

    @Override
    public String toString() {

        return "class DataPipelineConfiguration {\n" +
                "    schemas: " + toIndentedString(schemas) + "\n" +
                "    startTime: " + toIndentedString(startTime) + "\n" +
                "}";
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

