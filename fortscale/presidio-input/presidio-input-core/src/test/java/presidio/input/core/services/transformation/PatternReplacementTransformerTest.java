package presidio.input.core.services.transformation;

import fortscale.domain.core.EventResult;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.junit4.SpringRunner;
import presidio.input.core.services.transformation.managers.AuthenticationTransformerManager;
import presidio.sdk.api.domain.AbstractInputDocument;
import presidio.sdk.api.domain.rawevents.AuthenticationRawEvent;
import presidio.sdk.api.domain.transformedevents.AuthenticationTransformedEvent;

import java.time.Instant;
import java.util.Arrays;
import java.util.List;

/**
 * Created by Efrat Noam on 11/8/17.
 */
@RunWith(SpringRunner.class)
public class PatternReplacementTransformerTest {

    @Test
    public void testTransformAuthenticationEventResolvedSrcMachineName() {
        PatternReplacementTransformer patternReplacementTransformer =
                new PatternReplacementTransformer(
                        AuthenticationRawEvent.SRC_MACHINE_NAME_FIELD_NAME,
                        AuthenticationTransformedEvent.SRC_MACHINE_CLUSTER_FIELD_NAME,
                        AuthenticationTransformerManager.CLUSTER_REPLACEMENT_PATTERN,
                        "",
                        null,
                        AuthenticationTransformerManager.CLUSTER_POST_REPLACEMENT_CONDITION);

        AuthenticationRawEvent authRawEvent = createAuthenticationEvent(Instant.now(), "dwef043.fortscale.com");
        List<AbstractInputDocument> transformedEvents = patternReplacementTransformer.transform(Arrays.asList(new AuthenticationTransformedEvent(authRawEvent)));

        Assert.assertEquals("dwef.fortscale.com", ((AuthenticationTransformedEvent) transformedEvents.get(0)).getSrcMachineCluster());
    }

    private AuthenticationRawEvent createAuthenticationEvent(Instant eventDate, String srcMachineName) {
        return new AuthenticationRawEvent(eventDate,
                "eventId",
                "dataSource",
                "userId",
                "operationType",
                null,
                EventResult.SUCCESS,
                "userName",
                "userDisplayName",
                null,
                "srcMachineId",
                srcMachineName,
                "dstMachineId",
                "dstMachineName",
                "dstMachineDomain",
                "resultCode", "site");
    }
}
