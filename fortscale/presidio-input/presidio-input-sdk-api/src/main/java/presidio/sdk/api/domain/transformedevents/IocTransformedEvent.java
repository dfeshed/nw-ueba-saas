package presidio.sdk.api.domain.transformedevents;

import presidio.sdk.api.domain.rawevents.IocRawEvent;

public class IocTransformedEvent extends IocRawEvent {

    public IocTransformedEvent(IocRawEvent rawEvent) {
        super(rawEvent);
    }

}
