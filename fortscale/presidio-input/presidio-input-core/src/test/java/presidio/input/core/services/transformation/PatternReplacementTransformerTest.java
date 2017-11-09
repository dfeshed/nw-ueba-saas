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

        AuthenticationRawEvent authRawEvent = new AuthenticationRawEvent(Instant.now(), "eventId",
                "dataSource", "userId", "operationType", null,
                EventResult.SUCCESS, "userName", "userDisplayName", null,
                "srcMachineId", "dwef043.fortscale.com", "dstMachineId",
                "dstMachineName", "dstMachineDomain", "resultCode");
        List<AbstractInputDocument> transformedEvents = patternReplacementTransformer.transform(Arrays.asList(new AuthenticationTransformedEvent(authRawEvent)));

        Assert.assertEquals("dwef.fortscale.com", ((AuthenticationTransformedEvent)transformedEvents.get(0)).getSrcMachineCluster());
    }

    @Test
    public void testTransformAuthenticationEventUnresolvedSrcMachineName() {
        PatternReplacementTransformer patternReplacementTransformer =
                new PatternReplacementTransformer(
                        AuthenticationRawEvent.SRC_MACHINE_NAME_FIELD_NAME,
                        AuthenticationTransformedEvent.SRC_MACHINE_CLUSTER_FIELD_NAME,
                        AuthenticationTransformerManager.IP_ADDRESS_PATTERN,
                        "",
                        null,
                        null);

        AuthenticationRawEvent authRawEvent = new AuthenticationRawEvent(Instant.now(), "eventId",
                "dataSource", "userId", "operationType", null,
                EventResult.SUCCESS, "userName", "userDisplayName", null,
                "srcMachineId", "10.20.30.40", "dstMachineId",
                "dstMachineName", "dstMachineDomain", "resultCode");
        List<AbstractInputDocument> transformedEvents = patternReplacementTransformer.transform(Arrays.asList(new AuthenticationTransformedEvent(authRawEvent)));

        Assert.assertEquals("", ((AuthenticationTransformedEvent)transformedEvents.get(0)).getSrcMachineCluster());
    }
}
