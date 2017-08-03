package presidio.ade.domain.record.enriched.authentication;

import org.springframework.data.annotation.Transient;

public interface AdeAuthenticationRecord {
    public static final String AUTHENTICATION_STR = "authentication";

    @Transient
    default String getAdeEventType() {
        return AUTHENTICATION_STR;
    }
}
