package presidio.input.core.services.transformation.managers;

import fortscale.common.general.Schema;
import fortscale.domain.core.EventResult;
import fortscale.utils.transform.AbstractJsonObjectTransformer;
import fortscale.utils.transform.IJsonObjectTransformer;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import presidio.input.core.services.transformation.TransformationService;
import presidio.input.core.spring.InputConfigTest;
import presidio.sdk.api.domain.AbstractInputDocument;
import presidio.sdk.api.domain.rawevents.AuthenticationRawEvent;
import presidio.sdk.api.domain.transformedevents.AuthenticationTransformedEvent;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author Efrat Noam
 */
@RunWith(SpringRunner.class)
@ContextConfiguration(classes = {InputConfigTest.class})
public class AuthenticationTransformationServiceTest {
    @Autowired
    private TransformationService transformationService;
    private Instant endDate = Instant.now();

    private List<IJsonObjectTransformer> transformers = new ArrayList<>();

    @Test
    public void testRunAuthenticationSchemaSrcMachineTransformations_unresolvedMachineNameAndId() {
        AuthenticationRawEvent authenticationRawEvent = createAuthenticationEvent(Instant.now(), "10.20.3.40", "1.34.56.255", "12.4.6.74", "10.65.20.88");
        List<AbstractInputDocument> events = Collections.singletonList(authenticationRawEvent);
        List<AbstractInputDocument> transformedEvents = transformationService.run(events, Schema.AUTHENTICATION, endDate, transformers);
        Assert.assertEquals("", ((AuthenticationTransformedEvent)transformedEvents.get(0)).getSrcMachineCluster());
        Assert.assertEquals("", ((AuthenticationTransformedEvent)transformedEvents.get(0)).getSrcMachineId());
        Assert.assertEquals("", ((AuthenticationTransformedEvent)transformedEvents.get(0)).getDstMachineCluster());
        Assert.assertEquals("", ((AuthenticationTransformedEvent)transformedEvents.get(0)).getDstMachineId());
    }

    @Test
    public void testRunAuthenticationSchemaSrcMachineTransformations_resolvedMachineNameAndId() {
        AuthenticationRawEvent authenticationRawEvent = createAuthenticationEvent(Instant.now(), "nameSPBGDCW01.prod.quest.corp", "idSPBGDCW01.prod.quest.corp", "nameSPBGDCW02.prod.quest.corp", "idSPBGDCW02.prod.quest.corp");
        List<AbstractInputDocument> events = Collections.singletonList(authenticationRawEvent);
        List<AbstractInputDocument> transformedEvents = transformationService.run(events, Schema.AUTHENTICATION, endDate, transformers);
        Assert.assertEquals("nameSPBGDCW.prod.quest.corp", ((AuthenticationTransformedEvent)transformedEvents.get(0)).getSrcMachineCluster());
        Assert.assertEquals("idSPBGDCW01.prod.quest.corp", ((AuthenticationTransformedEvent)transformedEvents.get(0)).getSrcMachineId());
        Assert.assertEquals("nameSPBGDCW.prod.quest.corp", ((AuthenticationTransformedEvent)transformedEvents.get(0)).getDstMachineCluster());
        Assert.assertEquals("idSPBGDCW02.prod.quest.corp", ((AuthenticationTransformedEvent)transformedEvents.get(0)).getDstMachineId());
    }

    private AuthenticationRawEvent createAuthenticationEvent(Instant eventDate, String srcMachineName, String srcMachineId, String dstMachineName, String dstMachineId) {
        return new AuthenticationRawEvent(eventDate, "eventId", "dataSource", "userId", "operationType", null, EventResult.SUCCESS, "userName", "userDisplayName", null, srcMachineId, srcMachineName, dstMachineId, dstMachineName, "dstMachineDomain", "resultCode", "site", "country", "city");
    }
}