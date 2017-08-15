package presidio.ade.domain.record.enriched.authentication;


import fortscale.domain.core.EventResult;
import presidio.ade.domain.record.enriched.BaseEnrichedContext;

public class AdeEnrichedAuthenticationContext extends BaseEnrichedContext {
    private String userId;
    private EventResult result;

    public AdeEnrichedAuthenticationContext() {
        super();
    }

    public AdeEnrichedAuthenticationContext(EnrichedAuthenticationRecord enrichedAuthenticationRecord) {
        super(enrichedAuthenticationRecord.getEventId());
        this.userId = enrichedAuthenticationRecord.getUserId();
        this.result = enrichedAuthenticationRecord.getResult();
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public EventResult getResult() {
        return result;
    }

    public void setResult(EventResult result) {
        this.result = result;
    }
}
