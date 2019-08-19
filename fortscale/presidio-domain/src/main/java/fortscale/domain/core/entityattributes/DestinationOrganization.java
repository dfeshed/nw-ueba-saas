package fortscale.domain.core.entityattributes;

public class DestinationOrganization extends EntityAttributes {

    public DestinationOrganization(String name, boolean isNewOccurrence) {
        super(name, isNewOccurrence);
    }

    // Dummy constructor required for jackson deserialization
    public DestinationOrganization() {}
}
