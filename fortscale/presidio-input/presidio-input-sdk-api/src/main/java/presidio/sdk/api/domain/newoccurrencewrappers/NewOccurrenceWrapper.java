package presidio.sdk.api.domain.newoccurrencewrappers;

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
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        NewOccurrenceWrapper newOccurrenceHolder = (NewOccurrenceWrapper) o;
        return isNewOccurrence == newOccurrenceHolder.isNewOccurrence &&
                Objects.equals(name, newOccurrenceHolder.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, isNewOccurrence);
    }

    @Override
    public String toString() {
        return "NewOccurrenceWrapper{" +
                "name='" + name + '\'' +
                ", isNewOccurrence=" + isNewOccurrence +
                '}';
    }
}
