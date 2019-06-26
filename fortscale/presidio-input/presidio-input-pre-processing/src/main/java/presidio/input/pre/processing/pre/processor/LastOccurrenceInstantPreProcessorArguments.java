package presidio.input.pre.processing.pre.processor;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import fortscale.common.general.Schema;
import org.apache.commons.lang3.Validate;

import java.time.Instant;
import java.util.List;

@JsonAutoDetect(
        creatorVisibility = JsonAutoDetect.Visibility.ANY,
        fieldVisibility = JsonAutoDetect.Visibility.NONE,
        getterVisibility = JsonAutoDetect.Visibility.NONE,
        isGetterVisibility = JsonAutoDetect.Visibility.NONE,
        setterVisibility = JsonAutoDetect.Visibility.NONE
)
public class LastOccurrenceInstantPreProcessorArguments {
    private final Instant startInstant;
    private final Instant endInstant;
    private final Schema schema;
    private final String instantFieldName;
    private final List<String> entityTypes;

    @JsonCreator
    public LastOccurrenceInstantPreProcessorArguments(
            @JsonProperty("startInstant") Instant startInstant,
            @JsonProperty("endInstant") Instant endInstant,
            @JsonProperty("schema") Schema schema,
            @JsonProperty("instantFieldName") String instantFieldName,
            @JsonProperty("entityTypes") List<String> entityTypes) {

        this.startInstant = Validate.notNull(startInstant, "startInstant cannot be null.");
        this.endInstant = Validate.notNull(endInstant, "endInstant cannot be null.");
        this.schema = Validate.notNull(schema, "schema cannot be null.");
        this.instantFieldName = Validate.notBlank(instantFieldName, "instantFieldName cannot be blank.");
        this.entityTypes = Validate.notEmpty(entityTypes, "entityTypes cannot be empty.");
        entityTypes.forEach(entityType -> Validate.notBlank(entityType, "entityTypes cannot contain blank elements."));
    }

    public Instant getStartInstant() {
        return startInstant;
    }

    public Instant getEndInstant() {
        return endInstant;
    }

    public Schema getSchema() {
        return schema;
    }

    public String getInstantFieldName() {
        return instantFieldName;
    }

    public List<String> getEntityTypes() {
        return entityTypes;
    }
}
