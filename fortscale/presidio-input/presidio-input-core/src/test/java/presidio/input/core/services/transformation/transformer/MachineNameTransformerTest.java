package presidio.input.core.services.transformation.transformer;

import fortscale.domain.core.EventResult;
import org.apache.commons.lang.StringUtils;
import org.junit.Assert;
import org.junit.Test;
import presidio.input.core.services.transformation.managers.AuthenticationTransformerManager;
import presidio.sdk.api.domain.AbstractInputDocument;
import presidio.sdk.api.domain.rawevents.AuthenticationRawEvent;
import presidio.sdk.api.domain.transformedevents.AuthenticationTransformedEvent;

import java.time.Instant;

/**
 * Created by barak_schuster on 11/9/17.
 */
public class MachineNameTransformerTest extends TransformerJsonTest {

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
                "dstMachineName", "dstMachineDomain", "resultCode", "site",
                "country", "city");
        AbstractInputDocument transformedEvents = MachineNameTransformer.transform(new AuthenticationTransformedEvent(authRawEvent));

        Assert.assertEquals("dwef.fortscale.com", ((AuthenticationTransformedEvent) transformedEvents).getSrcMachineCluster());
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
                "dstMachineName", "dstMachineDomain", "resultCode", "site",
                "country", "city");
        AbstractInputDocument transformedEvents = machineNameTransformer.transform(new AuthenticationTransformedEvent(authRawEvent));

        Assert.assertEquals(StringUtils.EMPTY, ((AuthenticationTransformedEvent) transformedEvents).getSrcMachineCluster());
    }

    @Test
    public void testTransformAuthenticationEventMachineNameNull() {
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
                "srcMachineId", null, "dstMachineId",
                "dstMachineName", "dstMachineDomain", "resultCode", "site",
                "country", "city");
        AbstractInputDocument transformedEvents = machineNameTransformer.transform(new AuthenticationTransformedEvent(authRawEvent));

        Assert.assertNull(((AuthenticationTransformedEvent) transformedEvents).getSrcMachineCluster());
    }

    @Override
    String getResourceFilePath() {
        return "MachineNameTransformer.json";
    }

    @Override
    Class getTransformerClass() {
        return MachineNameTransformer.class;
    }
}
