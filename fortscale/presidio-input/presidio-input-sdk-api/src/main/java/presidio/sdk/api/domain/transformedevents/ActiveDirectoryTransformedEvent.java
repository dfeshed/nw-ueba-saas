package presidio.sdk.api.domain.transformedevents;

import presidio.sdk.api.domain.rawevents.ActiveDirectoryRawEvent;

public class ActiveDirectoryTransformedEvent extends ActiveDirectoryRawEvent {
    public ActiveDirectoryTransformedEvent(ActiveDirectoryRawEvent rawEvent) {
        super(rawEvent);
    }
}
