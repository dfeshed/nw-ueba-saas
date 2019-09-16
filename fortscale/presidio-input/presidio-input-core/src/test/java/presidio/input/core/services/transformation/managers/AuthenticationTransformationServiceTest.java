package presidio.input.core.services.transformation.managers;

import fortscale.common.general.Schema;
import fortscale.domain.core.EventResult;
import fortscale.utils.transform.IJsonObjectTransformer;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import presidio.input.core.services.transformation.DeserializerTransformationService;
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

    @Autowired
    private DeserializerTransformationService deserializerTransformationService;

    private List<IJsonObjectTransformer> transformers = new ArrayList<>();

    @Test
    public void testRunAuthenticationSchemaSrcMachineTransformations_unresolvedMachineNameAndId() {
        String expected = "";
        AuthenticationRawEvent authenticationRawEvent = createAuthenticationEvent(Instant.now(), "10.20.3.40", "1.34.56.255", "12.4.6.74", "10.65.20.88");
        List<AbstractInputDocument> events = Collections.singletonList(authenticationRawEvent);
        List<AbstractInputDocument> transformedEvents = transformationService.run(events, Schema.AUTHENTICATION, endDate,
                deserializerTransformationService.getTransformers(Schema.AUTHENTICATION, endDate, endDate));
        AuthenticationTransformedEvent authenticationTransformedEvent = (AuthenticationTransformedEvent) transformedEvents.get(0);
        Assert.assertEquals(expected, authenticationTransformedEvent.getSrcMachineCluster());
        Assert.assertEquals(expected, authenticationTransformedEvent.getSrcMachineId());
        Assert.assertEquals(expected, authenticationTransformedEvent.getDstMachineCluster());
        Assert.assertEquals(expected, authenticationTransformedEvent.getDstMachineId());
    }

    @Test
    public void testRunAuthenticationSchemaSrcMachineTransformations_resolvedMachineNameAndId() {
        AuthenticationRawEvent authenticationRawEvent = createAuthenticationEvent(Instant.now(), "nameSPBGDCW01.prod.quest.corp", "idSPBGDCW01.prod.quest.corp", "nameSPBGDCW02.prod.quest.corp", "idSPBGDCW02.prod.quest.corp");
        List<AbstractInputDocument> events = Collections.singletonList(authenticationRawEvent);
        List<AbstractInputDocument> transformedEvents = transformationService.run(events, Schema.AUTHENTICATION, endDate,
                deserializerTransformationService.getTransformers(Schema.AUTHENTICATION, endDate, endDate));
        AuthenticationTransformedEvent authenticationTransformedEvent = (AuthenticationTransformedEvent) transformedEvents.get(0);
        Assert.assertEquals("nameSPBGDCW.prod.quest.corp", authenticationTransformedEvent.getSrcMachineCluster());
        Assert.assertEquals("idSPBGDCW01.prod.quest.corp", authenticationTransformedEvent.getSrcMachineId());
        Assert.assertEquals("nameSPBGDCW.prod.quest.corp", authenticationTransformedEvent.getDstMachineCluster());
        Assert.assertEquals("idSPBGDCW02.prod.quest.corp", authenticationTransformedEvent.getDstMachineId());
    }

    private AuthenticationRawEvent createAuthenticationEvent(Instant eventDate, String srcMachineName, String srcMachineId, String dstMachineName, String dstMachineId) {
        return new AuthenticationRawEvent(eventDate, "eventId", "dataSource", "userId", "operationType", null, EventResult.SUCCESS, "userName", "userDisplayName", null, srcMachineId, srcMachineName, dstMachineId, dstMachineName, "dstMachineDomain", "resultCode", "site", "country", "city");
    }
}