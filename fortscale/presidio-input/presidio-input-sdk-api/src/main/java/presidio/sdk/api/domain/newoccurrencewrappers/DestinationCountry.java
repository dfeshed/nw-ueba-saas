package presidio.sdk.api.domain.newoccurrencewrappers;

public class DestinationCountry extends NewOccurrenceWrapper {

    public DestinationCountry(String name, boolean isNewOccurrence) {
        this.name = name;
        this.isNewOccurrence = isNewOccurrence;
    }

    // Dummy constructor required for jackson deserialization
    public DestinationCountry() {}
}
