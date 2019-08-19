package fortscale.domain.core.entityattributes;

public class SslSubject extends EntityAttributes {

    public SslSubject(String name, boolean isNewOccurrence) {
        super(name, isNewOccurrence);
    }

    // Dummy constructor required for jackson deserialization
    public SslSubject() {}
}
