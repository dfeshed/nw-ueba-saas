package presidio.manager.webapp.configuration;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import fortscale.common.general.Schema;
import org.apache.commons.lang3.Validate;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

import javax.annotation.Generated;
import java.time.Instant;
import java.util.List;

@Generated(value = "class io.swagger.codegen.languages.SpringCodegen", date = "2019-04-15T00:00:00.000Z")
public class DataPipelineConfiguration {
    private List<Schema> schemas;
    private Instant startTime;

    @JsonCreator
    public DataPipelineConfiguration(
            @JsonProperty("schemas") List<Schema> schemas,
            @JsonProperty("startTime") Instant startTime) {

        this.schemas = Validate.notEmpty(schemas, "schemas cannot be empty");
        this.startTime = Validate.notNull(startTime, "startTime cannot be null");
        schemas.forEach(schema -> Validate.notNull(schema, "schemas cannot contain null elements"));
    }

    public List<Schema> getSchemas() {
        return schemas;
    }

    public void setSchemas(List<Schema> schemas) {
        this.schemas = schemas;
    }

    public Instant getStartTime() {
        return startTime;
    }

    public void setStartTime(Instant startTime) {
        this.startTime = startTime;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) return true;
        if (!(object instanceof DataPipelineConfiguration)) return false;
        DataPipelineConfiguration that = (DataPipelineConfiguration)object;
        return new EqualsBuilder()
                .append(schemas, that.schemas)
                .append(startTime, that.startTime)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(schemas)
                .append(startTime)
                .toHashCode();
    }
}
