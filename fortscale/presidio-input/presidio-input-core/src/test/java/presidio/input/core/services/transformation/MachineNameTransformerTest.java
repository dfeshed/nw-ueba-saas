package presidio.input.core.services.transformation;

import fortscale.domain.core.EventResult;
import org.apache.commons.lang.StringUtils;
import org.junit.Assert;
import org.junit.Test;
import presidio.input.core.services.transformation.managers.AuthenticationTransformerManager;
import presidio.sdk.api.domain.AbstractInputDocument;
import presidio.sdk.api.domain.rawevents.AuthenticationRawEvent;
import presidio.sdk.api.domain.transformedevents.AuthenticationTransformedEvent;

import java.time.Instant;
import java.util.Arrays;
import java.util.List;

/**
 * Created by barak_schuster on 11/9/17.
 */
public class MachineNameTransformerTest {

    @Test
    public void testTransformAuthenticationEventResolvedSrcMachineName() {
        MachineNameTransformer MachineNameTransformer =
                new MachineNameTransformer(
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
                "dstMachineName", "dstMachineDomain", "resultCode", "site");
        List<AbstractInputDocument> transformedEvents = MachineNameTransformer.transform(Arrays.asList(new AuthenticationTransformedEvent(authRawEvent)));

        Assert.assertEquals("dwef.fortscale.com", ((AuthenticationTransformedEvent) transformedEvents.get(0)).getSrcMachineCluster());
    }

    @Test
    public void testTransformAuthenticationEventUnresolvedSrcMachineName() {
        MachineNameTransformer machineNameTransformer =
                new MachineNameTransformer(
                        AuthenticationRawEvent.SRC_MACHINE_NAME_FIELD_NAME,
                        AuthenticationTransformedEvent.SRC_MACHINE_CLUSTER_FIELD_NAME,
                        AuthenticationTransformerManager.CLUSTER_REPLACEMENT_PATTERN,
                        "",
                        null,
                        AuthenticationTransformerManager.CLUSTER_POST_REPLACEMENT_CONDITION);

        AuthenticationRawEvent authRawEvent = new AuthenticationRawEvent(Instant.now(), "eventId",
                "dataSource", "userId", "operationType", null,
                EventResult.SUCCESS, "userName", "userDisplayName", null,
                "srcMachineId", "10.20.3.40", "dstMachineId",
                "dstMachineName", "dstMachineDomain", "resultCode", "site");
        List<AbstractInputDocument> transformedEvents = machineNameTransformer.transform(Arrays.asList(new AuthenticationTransformedEvent(authRawEvent)));

        Assert.assertEquals(StringUtils.EMPTY, ((AuthenticationTransformedEvent) transformedEvents.get(0)).getSrcMachineCluster());
    }
}
