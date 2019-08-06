package presidio.sdk.api.domain.newoccurrencewrappers;

public class Ja3 extends NewOccurrenceWrapper {

    public Ja3(String name, boolean isNewOccurrence) {
        this.name = name;
        this.isNewOccurrence = isNewOccurrence;
    }

    // Dummy constructor required for jackson deserialization
    public Ja3() {}
}
