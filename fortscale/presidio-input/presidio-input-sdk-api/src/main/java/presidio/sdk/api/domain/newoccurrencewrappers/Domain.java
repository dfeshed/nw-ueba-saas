package presidio.sdk.api.domain.newoccurrencewrappers;

public class Domain extends NewOccurrenceWrapper {

    public Domain(String domainName, boolean isNewOccurrence) {
        this.name = domainName;
        this.isNewOccurrence = isNewOccurrence;
    }

    // Dummy constructor required for jackson deserialization
    public Domain() {}

}
