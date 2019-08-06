package presidio.sdk.api.domain.newoccurrencewrappers;

public class DestinationOrganization extends NewOccurrenceWrapper {

    public DestinationOrganization(String name, boolean isNewOccurrence) {
        this.name = name;
        this.isNewOccurrence = isNewOccurrence;
    }

    // Dummy constructor required for jackson deserialization
    public DestinationOrganization() {}
}
