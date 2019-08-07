package presidio.sdk.api.domain.newoccurrencewrappers;

public class DestinationAutonomousSystemNumber extends NewOccurrenceWrapper {

    public DestinationAutonomousSystemNumber(String name, boolean isNewOccurrence) {
        this.name = name;
        this.isNewOccurrence = isNewOccurrence;
    }

    // Dummy constructor required for jackson deserialization
    public DestinationAutonomousSystemNumber() {}
}
