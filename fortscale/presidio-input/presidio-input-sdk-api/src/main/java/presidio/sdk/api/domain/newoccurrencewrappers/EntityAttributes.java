package presidio.sdk.api.domain.newoccurrencewrappers;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;

import java.util.Objects;

abstract public class EntityAttributes {

    protected String name;
    protected boolean isNewOccurrence;

    public String getName() {
        return name;
    }

    public boolean getIsNewOccurrence() {
        return isNewOccurrence;
    }

    public EntityAttributes(String name, boolean isNewOccurrence) {
        this.name = name;
        this.isNewOccurrence = isNewOccurrence;
    }

    public EntityAttributes() {}

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
