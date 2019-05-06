package presidio.sdk.api.domain.transformedevents;

import presidio.sdk.api.domain.rawevents.TlsRawEvent;

public class TlsTransformedEvent extends TlsRawEvent {

    public TlsTransformedEvent(TlsRawEvent tlsRawEvent){
        super(tlsRawEvent);
    }
}
