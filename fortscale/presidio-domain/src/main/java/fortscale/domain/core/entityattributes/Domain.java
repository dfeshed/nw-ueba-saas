package fortscale.domain.core.entityattributes;

public class Domain extends EntityAttributes {

    public Domain(String name, boolean isNewOccurrence) {
        super(name, isNewOccurrence);    }

    // Dummy constructor required for jackson deserialization
    public Domain() {}
}
