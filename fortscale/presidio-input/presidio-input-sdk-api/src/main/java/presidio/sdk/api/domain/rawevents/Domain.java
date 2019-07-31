package presidio.sdk.api.domain.rawevents;

import org.springframework.data.mongodb.core.mapping.Field;

import java.io.Serializable;

public class Domain implements Serializable {

    private static final String DOMAIN_FIELD_NAME = "domainName";
    private static final String NEW_OCCURRENCE_FIELD_NAME = "isNewOccurrence";

    @Field(DOMAIN_FIELD_NAME)
    private String domainName;

    @Field(NEW_OCCURRENCE_FIELD_NAME)
    private boolean isNewOccurrence;


    public boolean isNewOccurrence() {
        return isNewOccurrence;
    }

    public Domain(String domainName, boolean isNewOccurrence) {
        this.domainName = domainName;
        this.isNewOccurrence = isNewOccurrence;
    }
}
