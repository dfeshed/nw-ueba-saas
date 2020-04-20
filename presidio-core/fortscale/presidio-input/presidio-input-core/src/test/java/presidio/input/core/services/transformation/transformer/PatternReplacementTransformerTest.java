package presidio.input.core.services.transformation.transformer;

import fortscale.domain.core.EventResult;
import fortscale.utils.transform.PatternReplacementTransformer;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.junit4.SpringRunner;
import presidio.input.core.services.transformation.managers.AuthenticationTransformerManager;
import presidio.sdk.api.domain.rawevents.AuthenticationRawEvent;
import presidio.sdk.api.domain.transformedevents.AuthenticationTransformedEvent;

import java.io.IOException;
import java.time.Instant;

/**
 * Created by Efrat Noam on 11/8/17.
 */
@RunWith(SpringRunner.class)
public class PatternReplacementTransformerTest extends TransformerJsonTest {
    public static final String CLUSTER_REPLACEMENT_PATTERN = "[0-9]";
    public static final String CLUSTER_POST_REPLACEMENT_CONDITION = "(.*[a-zA-Z]){5}.*";

    @Test
    public void testTransformAuthenticationEventResolvedSrcMachineName() throws IOException {
        PatternReplacementTransformer patternReplacementTransformer =
                new PatternReplacementTransformer("name",
                        AuthenticationRawEvent.SRC_MACHINE_NAME_FIELD_NAME,
                        AuthenticationTransformedEvent.SRC_MACHINE_CLUSTER_FIELD_NAME,
                        CLUSTER_REPLACEMENT_PATTERN,
                        "",
                        null,
                        CLUSTER_POST_REPLACEMENT_CONDITION);

        AuthenticationRawEvent authRawEvent = createAuthenticationEvent(Instant.now(), "dwef043.fortscale.com");
        AuthenticationTransformedEvent authenticationTransformedEvent = (AuthenticationTransformedEvent) transformEvent(authRawEvent, patternReplacementTransformer, AuthenticationTransformedEvent.class);
        Assert.assertEquals("dwef.fortscale.com", authenticationTransformedEvent.getSrcMachineCluster());
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
                "resultCode", "site", "country", "city");
    }

    @Override
    String getResourceFilePath() {
        return "PatternReplacementTransformer.json";
    }

    @Override
    Class getTransformerClass() {
        return PatternReplacementTransformer.class;
    }
}
