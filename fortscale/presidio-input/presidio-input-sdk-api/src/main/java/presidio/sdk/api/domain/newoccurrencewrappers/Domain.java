package presidio.sdk.api.domain.newoccurrencewrappers;

public class Domain extends NewOccurrenceWrapper {

    public Domain(String name, boolean isNewOccurrence) {
        super(name, isNewOccurrence);    }

    // Dummy constructor required for jackson deserialization
    public Domain() {}
}
