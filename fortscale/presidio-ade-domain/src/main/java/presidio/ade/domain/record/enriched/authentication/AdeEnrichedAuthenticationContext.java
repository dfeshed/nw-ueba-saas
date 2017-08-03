package presidio.ade.domain.record.enriched.authentication;


import fortscale.domain.core.EventResult;

public class AdeEnrichedAuthenticationContext {
    private String userId;
    private EventResult result;


    public AdeEnrichedAuthenticationContext(EnrichedAuthenticationRecord enrichedAuthenticationRecord) {
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
