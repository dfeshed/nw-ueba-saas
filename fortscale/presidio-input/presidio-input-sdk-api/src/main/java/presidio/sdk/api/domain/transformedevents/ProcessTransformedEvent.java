package presidio.sdk.api.domain.transformedevents;

import presidio.sdk.api.domain.rawevents.ProcessRawEvent;

public class ProcessTransformedEvent extends ProcessRawEvent {
    public ProcessTransformedEvent(ProcessRawEvent rawEvent) {
        super(rawEvent);
    }
}
