package presidio.sdk.api.domain.newoccurrencewrappers;

public class DestinationAsn extends EntityAttributes {

    public DestinationAsn(String name, boolean isNewOccurrence) {
        super(name, isNewOccurrence);
    }

    // Dummy constructor required for jackson deserialization
    public DestinationAsn() {}
}
