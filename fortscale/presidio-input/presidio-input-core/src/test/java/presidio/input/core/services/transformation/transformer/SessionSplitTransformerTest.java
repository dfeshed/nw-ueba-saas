package presidio.input.core.services.transformation.transformer;

import org.junit.Test;
import presidio.input.core.services.transformation.transformer.SessionSplitTransformer.SessionSplitTransformer;
import presidio.sdk.api.domain.rawevents.TlsRawEvent;
import presidio.sdk.api.domain.transformedevents.TlsTransformedEvent;

import java.util.Collections;
import java.util.HashMap;

public class SessionSplitTransformerTest {

    @Test
    public void testSessionSplitNull(){
        SessionSplitTransformer transformer = new SessionSplitTransformer(new HashMap<>(), 0);
        TlsTransformedEvent tlsTransformedEvent = new TlsTransformedEvent(new TlsRawEvent());
        transformer.transform(Collections.singletonList(tlsTransformedEvent));
    }
}
