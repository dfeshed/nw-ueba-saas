package presidio.sdk.api.domain.newoccurrencewrappers;

public class DestinationPort extends NewOccurrenceWrapper {

    public DestinationPort(String name, boolean isNewOccurrence) {
        this.name = name;
        this.isNewOccurrence = isNewOccurrence;
    }

    // Dummy constructor required for jackson deserialization
    public DestinationPort() {}
}
