package presidio.ade.domain.record.enriched;

import org.springframework.data.annotation.Transient;

public interface AuthenticationRecord {
    public static final String AUTHENTICATION_STR = "authentication";

    @Transient
    default String getEventType() {
        return AUTHENTICATION_STR;
    }
}
