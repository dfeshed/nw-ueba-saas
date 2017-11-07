package presidio.data.generators.event;

import org.junit.Assert;
import org.junit.Test;
import presidio.data.domain.event.activedirectory.ActiveDirectoryEvent;
import presidio.data.domain.event.authentication.AuthenticationEvent;
import presidio.data.domain.event.file.FileEvent;
import presidio.data.generators.common.GeneratorException;
import presidio.data.generators.common.time.ITimeGenerator;
import presidio.data.generators.common.time.TimeGenerator;
import presidio.data.generators.event.activedirectory.ActiveDirectoryEventsGenerator;
import presidio.data.generators.event.authentication.AuthenticationEventsGenerator;
import presidio.data.generators.event.file.FileEventsGenerator;

import java.time.LocalTime;
import java.util.List;

public class AbstractEventsGeneratorUsageTest {

    @Test
    public void AuthenticationBulkEventsGenerator() throws GeneratorException {
        final int BULK_SIZE = 10;
        List<AuthenticationEvent> events = null;
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
        List<AuthenticationEvent> events = null;
        AuthenticationEventsGenerator generator = new AuthenticationEventsGenerator();

        events = generator.generate();
        Assert.assertEquals(1392, events.size()); // all events for default time generator

        events = generator.generate(); // no more events
        Assert.assertEquals(0, events.size());
    }

    @Test
    public void AuthenticationCustomTimeEventsGenerator() throws GeneratorException {
        List<AuthenticationEvent> events = null;
        ITimeGenerator timeGen = new TimeGenerator(LocalTime.of(0,0,0,0), LocalTime.of(1,0,0,0), 100, 2, 1);

        AuthenticationEventsGenerator generator = new AuthenticationEventsGenerator();
        generator.setTimeGenerator(timeGen);

        events = generator.generate();
        Assert.assertEquals(10 * 60 * 60, events.size()); // all events for default time generator

        events = generator.generate(); // no more events
        Assert.assertEquals(0, events.size());
    }

    @Test
    public void ActiveDirectoryAllEventsGenerator() throws GeneratorException {
        List<ActiveDirectoryEvent> events = null;
        ActiveDirectoryEventsGenerator generator = new ActiveDirectoryEventsGenerator();

        events = generator.generate();
        Assert.assertEquals(1392, events.size()); // all events for default time generator

        events = generator.generate(); // no more events
        Assert.assertEquals(0, events.size());
    }

    @Test
    public void ActiveDirectoryBulkEventsGenerator() throws GeneratorException {
        final int BULK_SIZE = 10;
        List<ActiveDirectoryEvent> events = null;
        ActiveDirectoryEventsGenerator generator = new ActiveDirectoryEventsGenerator();

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
    public void FileBulkEventsGenerator() throws GeneratorException {
        final int BULK_SIZE = 10;
        List<FileEvent> events = null;
        FileEventsGenerator generator = new FileEventsGenerator();

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
    public void FileAllEventsGenerator() throws GeneratorException {
        List<FileEvent> events = null;
        FileEventsGenerator generator = new FileEventsGenerator();

        events = generator.generate();
        Assert.assertEquals(1392, events.size()); // all events for default time generator

        events = generator.generate(); // no more events
        Assert.assertEquals(0, events.size());
    }

}
