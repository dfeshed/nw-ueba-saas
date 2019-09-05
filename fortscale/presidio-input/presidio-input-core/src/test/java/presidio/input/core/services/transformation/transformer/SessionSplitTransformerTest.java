package presidio.input.core.services.transformation.transformer;

import fortscale.domain.core.entityattributes.*;
import org.junit.Test;
import presidio.input.core.services.transformation.transformer.SessionSplitTransformer.SessionSplitTransformer;
import presidio.sdk.api.domain.rawevents.TlsRawEvent;
import presidio.sdk.api.domain.transformedevents.TlsTransformedEvent;

import java.io.IOException;
import java.time.Instant;
import java.util.ArrayList;

public class SessionSplitTransformerTest extends TransformerJsonTest{

    @Test
    public void testSessionSplitNull() throws IOException {
        SessionSplitTransformer transformer = new SessionSplitTransformer("name", "tls", new ArrayList<>());
        TlsTransformedEvent tlsTransformedEvent = new TlsTransformedEvent(generateTlsRawEvent());
        //transformEvent(tlsTransformedEvent, transformer, TlsTransformedEvent.class);
    }

    private TlsRawEvent generateTlsRawEvent() {
        Instant laterInstant = Instant.now().plusSeconds(10000L * 182 * 60 * 60);
        return new TlsRawEvent(laterInstant, "TLS", "dataSource", null, "", "", "", "",
                new DestinationCountry("dstCountry"),
                new SslSubject("ssl"), new Domain("google.com"),
                new DestinationOrganization("dstOrg"),
                new DestinationAsn("dstAsn"), 0L, 0L, "", "",
                new Ja3("ja3"), "", "",
                new DestinationPort("dstPort"), null, null, 2);
    }

    @Override
    String getResourceFilePath() {
        return "SessionSplitTransformer.json";
    }

    @Override
    Class getTransformerClass() {
        return SessionSplitTransformer.class;
    }
}
