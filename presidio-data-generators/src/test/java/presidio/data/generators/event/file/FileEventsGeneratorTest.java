package presidio.data.generators.event.file;

import org.junit.Assert;
import org.junit.Test;
import presidio.data.domain.event.file.FileEvent;
import presidio.data.generators.common.GeneratorException;

import java.util.List;

/**
 * Created by presidio on 11/7/17.
 */
public class FileEventsGeneratorTest {
    @Test
    public void FileBulkEventsGenerator() throws GeneratorException {
        final int BULK_SIZE = 10;
        List<FileEvent> events;
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
        List<FileEvent> events;
        FileEventsGenerator generator = new FileEventsGenerator();

        events = generator.generate();
        Assert.assertEquals(1392, events.size()); // all events for default time generator

        events = generator.generate(); // no more events
        Assert.assertEquals(0, events.size());
    }
}
