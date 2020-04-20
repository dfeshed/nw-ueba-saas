package fortscale.domain.core.entityattributes;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.apache.commons.lang.Validate;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;

import java.util.Objects;

@JsonAutoDetect(fieldVisibility = Visibility.ANY, getterVisibility = Visibility.NONE, setterVisibility = Visibility.NONE)
public abstract class EntityAttributes {

    @JsonProperty("name")
    protected String name;
    @JsonProperty("isNewOccurrence")
    private Boolean isNewOccurrence;

    public String getName() {
        return name;
    }

    public Boolean getIsNewOccurrence() {
        return isNewOccurrence;
    }

    public void setNewOccurrence(Boolean newOccurrence) {
        isNewOccurrence = newOccurrence;
    }

    @JsonCreator
    EntityAttributes(@JsonProperty("name") String name) {
        Validate.notNull(name);
        this.name = name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        EntityAttributes entityAttributes = (EntityAttributes) o;
        return new EqualsBuilder().append(isNewOccurrence, entityAttributes.isNewOccurrence)
                                  .append(name, entityAttributes.name).isEquals();
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, isNewOccurrence);
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }
}
