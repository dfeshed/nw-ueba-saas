package presidio.data.generators.event.registry;

import org.junit.Assert;
import org.junit.Test;
import presidio.data.domain.event.process.ProcessEvent;
import presidio.data.domain.event.registry.RegistryEvent;
import presidio.data.generators.common.GeneratorException;
import presidio.data.generators.event.process.ProcessEventsGenerator;

import java.util.List;

/**
 * Created by presidio on 11/7/17.
 */
public class RegistryEventsGeneratorTest {
    @Test
    public void ProceessAllEventsGenerator() throws GeneratorException {
        List<RegistryEvent> events;
        RegistryEventsGenerator generator = new RegistryEventsGenerator();

        events = generator.generate();
        Assert.assertEquals(1392, events.size()); // all events for default time generator

        events = generator.generate(); // no more events
        Assert.assertEquals(0, events.size());
    }
}
