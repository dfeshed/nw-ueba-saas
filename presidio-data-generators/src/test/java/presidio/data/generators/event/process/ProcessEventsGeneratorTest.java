package presidio.data.generators.event.process;

import org.junit.Assert;
import org.junit.Test;
import presidio.data.domain.event.process.ProcessEvent;
import presidio.data.generators.common.GeneratorException;

import java.util.List;

/**
 * Created by presidio on 11/7/17.
 */
public class ProcessEventsGeneratorTest {
    @Test
    public void ProceessAllEventsGenerator() throws GeneratorException {
        List<ProcessEvent> events;
        ProcessEventsGenerator generator = new ProcessEventsGenerator();

        events = generator.generate();
        Assert.assertEquals(1392, events.size()); // all events for default time generator

        events = generator.generate(); // no more events
        Assert.assertEquals(0, events.size());
    }
}
