package presidio.sdk.api.domain.newoccurrencewrappers;

public class SslSubject extends NewOccurrenceWrapper {

    public SslSubject(String name, boolean isNewOccurrence) {
        super(name, isNewOccurrence);
    }

    // Dummy constructor required for jackson deserialization
    public SslSubject() {}
}
