package presidio.sdk.api.domain.rawevents;

import java.util.Objects;

public class Domain {

    private String name;
    private boolean isNewOccurrence;

    public Domain(String domainName, boolean isNewOccurrence) {
        this.name = domainName;
        this.isNewOccurrence = isNewOccurrence;
    }

    // Dummy constructor required for jackson deserialization
    public Domain() {}

    public String getName() {
        return name;
    }

    public boolean getIsNewOccurrence() {
        return isNewOccurrence;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setIsNewOccurrence(boolean newOccurrence) {
        isNewOccurrence = newOccurrence;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Domain domain = (Domain) o;
        return isNewOccurrence == domain.isNewOccurrence &&
                Objects.equals(name, domain.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, isNewOccurrence);
    }
}
