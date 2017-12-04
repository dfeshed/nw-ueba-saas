package presidio.data.generators.event.authentication;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import presidio.data.domain.event.authentication.AUTHENTICATION_OPERATION_TYPE;
import presidio.data.domain.event.authentication.AuthenticationEvent;
import presidio.data.generators.authenticationop.AuthenticationOpTypeCategoriesGenerator;
import presidio.data.generators.authenticationop.AuthenticationOperationGenerator;
import presidio.data.generators.common.GeneratorException;
import presidio.data.generators.common.time.ITimeGenerator;
import presidio.data.generators.common.time.TimeGenerator;

import java.time.LocalTime;
import java.util.List;

public class AuthenticationEventsGeneratorTest {

    List<AuthenticationEvent> events;

    /** Default values:
     * time: 8:00 to 16:00, every 10 min, 30 to 1 days back
     * authenticationop type: all types from enum presidio.data.domain.authenticationop.AUTHENTICATION_OPERATION_TYPE, not blank
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
        Assert.assertEquals("Logon Activity", events.get(0).getDataSource());
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
    public void DstMachineId () {
        Assert.assertEquals(14 , events.get(0).getDstMachineEntity().getMachineId().length());
    }

    @Test
    public void SrcMachineId () {
        Assert.assertEquals(14, events.get(0).getSrcMachineEntity().getMachineId().length());
    }

    @Test
    public void AuthenticationTypeTest () {
        // Operation types - see that all included, in the same order as enum
        Assert.assertEquals(AUTHENTICATION_OPERATION_TYPE.USER_FAILED_TO_LOG_ON_INTERACTIVELY.value, events.get(0).getAuthenticationOperation().getOperationType().getName());
        Assert.assertEquals(AUTHENTICATION_OPERATION_TYPE.USER_FAILED_TO_LOG_ON_INTERACTIVELY_FROM_A_REMOTE_COMPUTER.value, events.get(1).getAuthenticationOperation().getOperationType().getName());
        Assert.assertEquals(AUTHENTICATION_OPERATION_TYPE.USER_FAILED_TO_AUTHENTICATE_THROUGH_KERBEROS.value, events.get(2).getAuthenticationOperation().getOperationType().getName());
        Assert.assertEquals(AUTHENTICATION_OPERATION_TYPE.USER_LOGGED_ON_INTERACTIVELY.value, events.get(3).getAuthenticationOperation().getOperationType().getName());
        Assert.assertEquals(AUTHENTICATION_OPERATION_TYPE.USER_LOGGED_ON_INTERACTIVELY_FROM_A_REMOTE_COMPUTER.value, events.get(4).getAuthenticationOperation().getOperationType().getName());
        Assert.assertEquals(AUTHENTICATION_OPERATION_TYPE.USER_AUTHENTICATED_THROUGH_KERBEROS.value, events.get(5).getAuthenticationOperation().getOperationType().getName());
    }

    @Test
    public void NormalizedUserNameTest () {
        Assert.assertEquals(10, events.get(0).getUser().getUserId().length());
    }

    @Test
    public void AuthenticationBulkEventsGenerator() throws GeneratorException {
        final int BULK_SIZE = 10;
        List<AuthenticationEvent> events;
        AuthenticationEventsGenerator generator = new AuthenticationEventsGenerator();

        events = generator.generate(BULK_SIZE);
        Assert.assertEquals(BULK_SIZE, events.size());

        events = generator.generate(0);
        Assert.assertEquals(0, events.size());

        events = generator.generate(1);
        Assert.assertEquals(1, events.size());

        events = generator.generate(100000);
        Assert.assertTrue(events.size() < 100000); // default time generator generates less than 100K events

        events = generator.generate(1); // no more events
        Assert.assertEquals(0, events.size());
    }

    @Test
    public void AuthenticationAllEventsGenerator() throws GeneratorException {
        List<AuthenticationEvent> events;
        AuthenticationEventsGenerator generator = new AuthenticationEventsGenerator();

        events = generator.generate();
        Assert.assertEquals(1392, events.size()); // all events for default time generator

        events = generator.generate(); // no more events
        Assert.assertEquals(0, events.size());
    }

    @Test
    public void AuthenticationCustomTimeEventsGenerator() throws GeneratorException {
        List<AuthenticationEvent> events;
        ITimeGenerator timeGen = new TimeGenerator(LocalTime.of(0,0,0,0), LocalTime.of(1,0,0,0), 100, 2, 1);

        AuthenticationEventsGenerator generator = new AuthenticationEventsGenerator(timeGen);

        events = generator.generate();
        Assert.assertEquals(10 * 60 * 60, events.size()); // all events for default time generator

        events = generator.generate(); // no more events
        Assert.assertEquals(0, events.size());
    }

    @Test
    public void AuthenticationByOneEventsGenerator() throws GeneratorException {
        AuthenticationEventsGenerator generator = new AuthenticationEventsGenerator();

        AuthenticationEvent event = generator.generateNext();
        Assert.assertEquals(generator.getTimeGenerator().getFirst(), event.getDateTime()); // all events for default time generator
    }

    @Test
    public void AuthenticationCategoryTypeEventsGenerator() throws GeneratorException {
        AuthenticationEventsGenerator generator = new AuthenticationEventsGenerator();
        AuthenticationEvent event = generator.generateNext();
        Assert.assertTrue(event.getAuthenticationOperation().getOperationType().getCategories().contains(""));

        event = generator.generateNext();
        Assert.assertTrue(event.getAuthenticationOperation().getOperationType().getCategories().contains("INTERACTIVE_REMOTE"));
    }
}
