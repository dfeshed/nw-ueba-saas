package presidio.sdk.api.domain.newoccurrencewrappers;

public class DestinationOrganization extends NewOccurrenceWrapper {

    public DestinationOrganization(String name, boolean isNewOccurrence) {
        super(name, isNewOccurrence);
    }

    // Dummy constructor required for jackson deserialization
    public DestinationOrganization() {}
}
