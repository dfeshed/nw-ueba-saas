package presidio.sdk.api.domain.newoccurrencewrappers;

public class DestinationCountry extends EntityAttributes {

    public DestinationCountry(String name, boolean isNewOccurrence) {
        super(name, isNewOccurrence);
    }

    // Dummy constructor required for jackson deserialization
    public DestinationCountry() {}
}
