package fortscale.domain.core.entityattributes;

public class DestinationPort extends EntityAttributes {

    public DestinationPort(String name, boolean isNewOccurrence) {
        super(name, isNewOccurrence);
    }

    // Dummy constructor required for jackson deserialization
    public DestinationPort() {}
}
