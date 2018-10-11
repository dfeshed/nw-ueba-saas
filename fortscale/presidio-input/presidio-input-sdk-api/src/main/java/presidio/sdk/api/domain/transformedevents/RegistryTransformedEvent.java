package presidio.sdk.api.domain.transformedevents;

import presidio.sdk.api.domain.rawevents.RegistryRawEvent;

public class RegistryTransformedEvent extends RegistryRawEvent {
    public RegistryTransformedEvent(RegistryRawEvent rawEvent) {
        super(rawEvent);
    }
}
