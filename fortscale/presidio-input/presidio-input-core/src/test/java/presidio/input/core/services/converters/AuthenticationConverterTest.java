package presidio.input.core.services.converters;

import fortscale.domain.core.EventResult;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.junit4.SpringRunner;
import presidio.input.core.services.converters.output.AuthenticationInputToOutputConverter;
import presidio.output.domain.records.events.AuthenticationEnrichedEvent;
import presidio.output.domain.records.events.EnrichedEvent;
import presidio.sdk.api.domain.rawevents.AuthenticationRawEvent;
import presidio.sdk.api.domain.transformedevents.AuthenticationTransformedEvent;

import java.time.Instant;

@RunWith(SpringRunner.class)
public class AuthenticationConverterTest {

    @Test
    public void testConverter_emptyMachineIdAndCluster() {
        AuthenticationInputToOutputConverter converter = new AuthenticationInputToOutputConverter();
        AuthenticationRawEvent rawEvent = new AuthenticationRawEvent(Instant.now(), "eventId",
                "dataSource", "userId", "operationType", null,
                EventResult.SUCCESS, "userName", "userDisplayName", null,
                "", "srcMachineName", "",
                "dstMachineName", "dstMachineDomain", "resultCode", "site");
        AuthenticationTransformedEvent inputRecord = new AuthenticationTransformedEvent(rawEvent);
        inputRecord.setDstMachineCluster("");
        inputRecord.setSrcMachineCluster("");

        EnrichedEvent outputRecord = converter.convert(inputRecord);
        AuthenticationEnrichedEvent expected = new AuthenticationEnrichedEvent();
        expected.setDstMachineDomain("dstMachineDomain");
        expected.setDstMachineId("dstMachineName");
        expected.setDstMachineNameRegexCluster("dstMachineName");
        expected.setSrcMachineId("srcMachineName");
        expected.setSrcMachineNameRegexCluster("srcMachineName");
        expected.setDataSource("dataSource");
        expected.setEventDate(Instant.now());
        expected.setOperationType("operationType");
        expected.setUserId("userId");

        AuthenticationEnrichedEvent authenticationOutputRecord = (AuthenticationEnrichedEvent) outputRecord;
        Assert.assertEquals(expected.getDstMachineDomain(), authenticationOutputRecord.getDstMachineDomain());
        Assert.assertEquals(expected.getDstMachineId(), authenticationOutputRecord.getDstMachineId());
        Assert.assertEquals(expected.getDstMachineNameRegexCluster(), authenticationOutputRecord.getDstMachineNameRegexCluster());
        Assert.assertEquals(expected.getSrcMachineId(), authenticationOutputRecord.getSrcMachineId());
        Assert.assertEquals(expected.getSrcMachineNameRegexCluster(), authenticationOutputRecord.getSrcMachineNameRegexCluster());
    }

    @Test
    public void testConverter_notEmptyMachineIdAndCluster() {
        AuthenticationInputToOutputConverter converter = new AuthenticationInputToOutputConverter();
        AuthenticationRawEvent rawEvent = new AuthenticationRawEvent(Instant.now(), "eventId",
                "dataSource", "userId", "operationType", null,
                EventResult.SUCCESS, "userName", "userDisplayName", null,
                "srcMachineId", "srcMachineName", "dstMachineId",
                "dstMachineName", "dstMachineDomain", "resultCode", "site");
        AuthenticationTransformedEvent inputRecord = new AuthenticationTransformedEvent(rawEvent);
        inputRecord.setDstMachineCluster("dstMachineCluster");
        inputRecord.setSrcMachineCluster("srcMachineCluster");

        EnrichedEvent outputRecord = converter.convert(inputRecord);
        AuthenticationEnrichedEvent expected = new AuthenticationEnrichedEvent();
        expected.setDstMachineDomain("dstMachineDomain");
        expected.setDstMachineId("dstMachineId");
        expected.setDstMachineNameRegexCluster("dstMachineCluster");
        expected.setSrcMachineId("srcMachineId");
        expected.setSrcMachineNameRegexCluster("srcMachineCluster");
        expected.setDataSource("dataSource");
        expected.setEventDate(Instant.now());
        expected.setOperationType("operationType");

        AuthenticationEnrichedEvent authenticationOutputRecord = (AuthenticationEnrichedEvent) outputRecord;
        Assert.assertEquals(expected.getDstMachineDomain(), authenticationOutputRecord.getDstMachineDomain());
        Assert.assertEquals(expected.getDstMachineId(), authenticationOutputRecord.getDstMachineId());
        Assert.assertEquals(expected.getDstMachineNameRegexCluster(), authenticationOutputRecord.getDstMachineNameRegexCluster());
        Assert.assertEquals(expected.getSrcMachineId(), authenticationOutputRecord.getSrcMachineId());
        Assert.assertEquals(expected.getSrcMachineNameRegexCluster(), authenticationOutputRecord.getSrcMachineNameRegexCluster());
    }

}
