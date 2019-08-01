package presidio.sdk.api.domain.rawevents;

import java.util.Objects;

public class Domain {

    private String domainName;
    private boolean isNewOccurrence;

    public Domain(String domainName, boolean isNewOccurrence) {
        this.domainName = domainName;
        this.isNewOccurrence = isNewOccurrence;
    }

    // Dummy constructor required for jackson deserialization
    public Domain() {}

    public String getDomainName() {
        return domainName;
    }

    public boolean getIsNewOccurrence() {
        return isNewOccurrence;
    }

    public void setDomainName(String domainName) {
        this.domainName = domainName;
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
                Objects.equals(domainName, domain.domainName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(domainName, isNewOccurrence);
    }
}
