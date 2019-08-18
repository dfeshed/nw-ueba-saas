package presidio.sdk.api.domain.newoccurrencewrappers;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;

import java.util.Objects;

abstract public class NewOccurrenceWrapper {

    protected String name;
    protected boolean isNewOccurrence;

    public String getName() {
        return name;
    }

    public boolean getIsNewOccurrence() {
        return isNewOccurrence;
    }

    public NewOccurrenceWrapper(String name, boolean isNewOccurrence) {
        this.name = name;
        this.isNewOccurrence = isNewOccurrence;
    }

    public NewOccurrenceWrapper() {}

    @Override
    public boolean equals(Object o) {
        return EqualsBuilder.reflectionEquals(this, o);
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
