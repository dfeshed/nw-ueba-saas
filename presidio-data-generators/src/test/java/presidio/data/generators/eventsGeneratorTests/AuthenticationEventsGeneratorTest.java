package presidio.data.generators.eventsGeneratorTests;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import presidio.data.generators.common.GeneratorException;
import presidio.data.domain.event.authentication.AUTHENTICATION_TYPE;
import presidio.data.domain.event.authentication.AuthenticationEvent;
import presidio.data.generators.event.authentication.AuthenticationEventsGenerator;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class AuthenticationEventsGeneratorTest {

    List<AuthenticationEvent> events;

    /** Default values:
     * time: 8:00 to 16:00, every 10 min, 30 to 1 days back
     * dataSource: "Quest"
     * authenticationop type: all types from enum presidio.data.domain.authenticationop.AUTHENTICATION_TYPE, not blank
     * eventId - unique
     * dstMachine: name - "random" alphanumeric string, 10 chars length, 2% remote
     * dstMachine: name - "random" alphanumeric string, 10 chars length
     * user: normalizedUsername - "random" alphanumeric string, 10 chars length
     * result: 100% "Success"
     * resultCode: "random" alphanumeric string, 10 chars length - TBD
     * event count: 1392 = 6 per hour * 8 work hours * 29 days
     */

    @Before
    public void prepare() throws GeneratorException {
        AuthenticationEventsGenerator generator = new AuthenticationEventsGenerator();
        events = generator.generate();
    }

    @Test
    public void EventsCountTest () {
        Assert.assertEquals(events.size(), 1392);
    }

    @Test
    public void DataSourceTest () {
        Assert.assertEquals("DefaultDS", events.get(0).getDataSource());
    }

    @Test
    public void ResultsTest () {
        // All should succeed
        boolean anySuccess = true; // expect to remain "true"
        for (AuthenticationEvent ev : events) {
            anySuccess = anySuccess && ev.getResult().equalsIgnoreCase("SUCCESS");
        }
        Assert.assertTrue(anySuccess);
    }

    @Test
    public void ResultCodeTest () {
        Assert.assertEquals(10, events.get(0).getResultCode().length());
    }

    @Test
    public void IsDstMachineRemotePctTest () {
        // remote dest machines - 2% of 1392 = 28
        int remotes = 0;
        for (AuthenticationEvent ev : events) {
            if (ev.getDstMachineEntity().isRemote()) remotes++;
        }
        Assert.assertEquals(28, remotes);
    }

    @Test
    public void NormalizedDstMachine () {
        Assert.assertEquals("host_1", events.get(0).getDstMachineEntity().getNormalizedMachinename());
    }

    @Test
    public void NormalizedSrcMachine () {
        Assert.assertEquals(14, events.get(0).getSrcMachineEntity().getNormalizedMachinename().length());
    }

    @Test
    public void AuthenticationTypeTest () {
        // Operation types - see that all included, in the same order as enum
        Assert.assertEquals(AUTHENTICATION_TYPE.AUTHENTICATION_TYPE_TBD.value, events.get(0).getAuthenticationType());
    }

    @Test
    public void NormalizedUserNameTest () {
        Assert.assertEquals(10, events.get(0).getUser().getUserId().length());
    }
}
