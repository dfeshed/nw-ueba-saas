package presidio.sdk.api.domain.newoccurrencewrappers;

public class SslSubject extends NewOccurrenceWrapper {

    public SslSubject(String name, boolean isNewOccurrence) {
        this.name = name;
        this.isNewOccurrence = isNewOccurrence;
    }

    // Dummy constructor required for jackson deserialization
    public SslSubject() {}
}
